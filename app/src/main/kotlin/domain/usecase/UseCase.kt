package com.pocketstudios.domain.usecase

abstract class UseCase<in P, out R> {
    abstract suspend operator fun invoke(params: P): R
}