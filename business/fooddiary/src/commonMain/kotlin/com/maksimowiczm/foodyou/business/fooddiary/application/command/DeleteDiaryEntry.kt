package com.maksimowiczm.foodyou.business.fooddiary.application.command

import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalDiaryEntryDataSource
import com.maksimowiczm.foodyou.business.shared.application.command.Command
import com.maksimowiczm.foodyou.business.shared.application.command.CommandHandler
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.error.logAndReturnFailure
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.persistence.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.shared.common.application.log.FoodYouLogger
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result
import kotlinx.coroutines.flow.first

class DeleteDiaryEntryCommand(val entryId: Long) : Command<Unit, DeleteDiaryEntryError>

sealed interface DeleteDiaryEntryError {
    data object EntryNotFound : DeleteDiaryEntryError
}

internal class DeleteDiaryEntryCommandHandler(
    private val localDiary: LocalDiaryEntryDataSource,
    private val transactionProvider: DatabaseTransactionProvider,
) : CommandHandler<DeleteDiaryEntryCommand, Unit, DeleteDiaryEntryError> {

    override suspend fun handle(
        command: DeleteDiaryEntryCommand
    ): Result<Unit, DeleteDiaryEntryError> {
        val entry = localDiary.observeEntry(command.entryId).first()

        if (entry == null) {
            return FoodYouLogger.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = DeleteDiaryEntryError.EntryNotFound,
                message = { "Diary entry with ID ${command.entryId} not found." },
            )
        }

        transactionProvider.withTransaction { localDiary.delete(entry) }

        return Ok(Unit)
    }

    private companion object {
        const val TAG = "DeleteDiaryEntryCommandHandler"
    }
}
