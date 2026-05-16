package com.pocketstudios.core.common

import kotlinx.coroutines.flow.Flow

abstract class UseCase<in Params, out Return> {
    abstract suspend fun execute(params: Params): Return

    suspend operator fun invoke(params: Params): Return = execute(params)
}

abstract class FlowUseCase<in Params, out Return> {
    abstract fun execute(params: Params): Flow<Return>

    operator fun invoke(params: Params): Flow<Return> = execute(params)
}

object NoParams
