import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

admin.initializeApp();
const db = admin.firestore();
const storage = admin.storage();

// ─── User Lifecycle ────────────────────────────────────────────────────────

/**
 * Triggered when a new user registers.
 * Creates their Firestore profile document.
 */
export const onUserCreated = functions.auth.user().onCreate(async (user) => {
  const userDoc = {
    uid: user.uid,
    displayName: user.displayName || "Creator",
    email: user.email || null,
    photoUrl: user.photoURL || null,
    isPro: false,
    proExpiry: null,
    proTier: null,
    projectCount: 0,
    totalExports: 0,
    storageUsedBytes: 0,
    fcmToken: null,
    createdAt: admin.firestore.FieldValue.serverTimestamp(),
    updatedAt: admin.firestore.FieldValue.serverTimestamp(),
  };

  await db.collection("users").doc(user.uid).set(userDoc);
  functions.logger.info(`Created user profile for ${user.uid}`);
});

/**
 * Triggered when a user is deleted.
 * Cleans up their Firestore data and Storage files.
 */
export const onUserDeleted = functions.auth.user().onDelete(async (user) => {
  // Delete Firestore data
  const batch = db.batch();
  batch.delete(db.collection("users").doc(user.uid));

  const projects = await db
    .collection("projects")
    .where("userId", "==", user.uid)
    .get();
  projects.docs.forEach((doc) => batch.delete(doc.ref));
  await batch.commit();

  // Delete Storage files
  const bucket = storage.bucket();
  await bucket.deleteFiles({ prefix: `users/${user.uid}/` });

  functions.logger.info(`Cleaned up data for deleted user ${user.uid}`);
});

// ─── Pro Purchase Webhook (RevenueCat) ────────────────────────────────────

/**
 * Webhook endpoint for RevenueCat purchase events.
 * Updates user's pro status in Firestore.
 */
export const onProPurchase = functions.https.onRequest(async (req, res) => {
  // Verify webhook signature
  const webhookSecret = functions.config().revenuecat?.webhook_secret;
  if (webhookSecret) {
    const signature = req.headers["x-revenuecat-signature"];
    if (!signature) {
      res.status(401).send("Unauthorized");
      return;
    }
    // TODO: Validate HMAC signature
  }

  const { event, app_user_id, expiration_at_ms, product_id } = req.body;

  const updateData: admin.firestore.UpdateData = {
    updatedAt: admin.firestore.FieldValue.serverTimestamp(),
  };

  switch (event) {
    case "INITIAL_PURCHASE":
    case "RENEWAL":
    case "PRODUCT_CHANGE":
      updateData["isPro"] = true;
      updateData["proExpiry"] = expiration_at_ms
        ? new Date(expiration_at_ms)
        : null;
      updateData["proTier"] = product_id || "unknown";
      break;

    case "EXPIRATION":
    case "CANCELLATION":
      updateData["isPro"] = false;
      updateData["proExpiry"] = null;
      updateData["proTier"] = null;
      break;

    default:
      functions.logger.info(`Unhandled RevenueCat event: ${event}`);
      res.status(200).send("OK");
      return;
  }

  try {
    await db.collection("users").doc(app_user_id).update(updateData);
    functions.logger.info(`Updated pro status for ${app_user_id}: event=${event}`);
    res.status(200).send("OK");
  } catch (error) {
    functions.logger.error("Failed to update pro status:", error);
    res.status(500).send("Internal Server Error");
  }
});

// ─── Export Notifications ─────────────────────────────────────────────────

/**
 * Sends a push notification when an export is completed.
 * Triggered on new document in /exports/{exportId}.
 */
export const onExportComplete = functions.firestore
  .document("exports/{exportId}")
  .onCreate(async (snap) => {
    const exportData = snap.data();
    const { userId, projectName, durationMs, resolutionLabel } = exportData;

    const userDoc = await db.collection("users").doc(userId).get();
    if (!userDoc.exists) return;

    const fcmToken = userDoc.data()?.fcmToken;
    if (!fcmToken) return;

    const durationSecs = Math.round((durationMs || 0) / 1000);
    const message: admin.messaging.Message = {
      token: fcmToken,
      notification: {
        title: "Export Ready! 🎬",
        body: `"${projectName}" (${durationSecs}s · ${resolutionLabel}) is ready to share`,
      },
      android: {
        notification: {
          channelId: "exports",
          icon: "ic_notification",
          color: "#CBB2FF",
          priority: "high",
        },
      },
      data: {
        type: "EXPORT_COMPLETE",
        exportId: snap.id,
        projectId: exportData.projectId || "",
      },
    };

    await admin.messaging().send(message);
    functions.logger.info(`Sent export notification to user ${userId}`);
  });

// ─── Scheduled Cleanup ────────────────────────────────────────────────────

/**
 * Deletes exports older than 90 days.
 * Runs daily at 3am UTC.
 */
export const cleanupOldExports = functions.pubsub
  .schedule("0 3 * * *")
  .timeZone("UTC")
  .onRun(async () => {
    const cutoffDate = new Date();
    cutoffDate.setDate(cutoffDate.getDate() - 90);

    const oldExports = await db
      .collection("exports")
      .where("createdAt", "<", cutoffDate)
      .limit(500) // Process in batches
      .get();

    if (oldExports.empty) {
      functions.logger.info("No old exports to clean up");
      return;
    }

    const batch = db.batch();
    const deletePromises: Promise<unknown>[] = [];

    oldExports.docs.forEach((doc) => {
      batch.delete(doc.ref);
      // Delete from Storage too
      const storagePath = doc.data().storagePath;
      if (storagePath) {
        deletePromises.push(
          storage.bucket().file(storagePath).delete().catch(() => {
            // File may already be deleted
          })
        );
      }
    });

    await batch.commit();
    await Promise.all(deletePromises);

    functions.logger.info(`Cleaned up ${oldExports.size} old exports`);
  });

// ─── FCM Token Update ─────────────────────────────────────────────────────

/**
 * HTTPS callable — updates user's FCM token.
 */
export const updateFcmToken = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "Must be authenticated");
  }

  const { token } = data;
  if (!token || typeof token !== "string") {
    throw new functions.https.HttpsError("invalid-argument", "Invalid FCM token");
  }

  await db.collection("users").doc(context.auth.uid).update({
    fcmToken: token,
    updatedAt: admin.firestore.FieldValue.serverTimestamp(),
  });

  return { success: true };
});
