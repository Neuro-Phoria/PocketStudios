import * as functions from 'firebase-functions';

export const onProjectCreated = functions.firestore
  .document('projects/{projectId}')
  .onCreate(async (snap, context) => {
    // Handle project creation
  });