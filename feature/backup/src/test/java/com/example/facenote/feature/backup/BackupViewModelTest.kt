package com.example.facenote.feature.backup

import androidx.lifecycle.SavedStateHandle
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.facenote.core.data.repository.BackupRepository
import com.example.facenote.core.model.DriveFile
import com.example.facenote.core.worker.BackupWorker
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class BackupViewModelTest {
	@OptIn(ExperimentalCoroutinesApi::class)
	private val testDispatcher = StandardTestDispatcher()
	private val testScope = TestScope(testDispatcher)

	private lateinit var viewModel: BackupViewModel
	private lateinit var mockRepository: BackupRepository
	private lateinit var mockWorker: WorkManager
	private lateinit var mockGoogleClient: GoogleSignInClient

	@OptIn(ExperimentalCoroutinesApi::class)
	@Before
	fun setUp() {
		Dispatchers.setMain(testDispatcher)

		mockRepository = mockk()
		mockWorker = mockk()
		mockGoogleClient = mockk()

		viewModel = BackupViewModel(
			context = mockk(),
			backupRepository = mockRepository,
			workManager = mockWorker,
			googleSignInClient = mockGoogleClient,
			savedStateHandle = SavedStateHandle(mapOf("shouldInitialize" to false)),
		)

	}

	@After
	fun tearDown(){
		Dispatchers.resetMain()
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@Test
	fun `backup should update inProgress state to true`() = testScope.runTest {
		val mockOperation = mockk<Operation>()

		val mockWorkInfo = mockk<WorkInfo>().apply {
			every { state } returns WorkInfo.State.RUNNING
			every { progress } returns workDataOf(
				"progress" to 0.5,
				"operation" to "Backing up"
			)
		}

		every {
			mockWorker.enqueueUniqueWork("backup-worker",ExistingWorkPolicy.REPLACE, any<OneTimeWorkRequest>())
		} returns mockOperation

		every {
			val backupWorkRequest = OneTimeWorkRequestBuilder<BackupWorker>().build()
			mockWorker.enqueueUniqueWork("backup-worker",ExistingWorkPolicy.REPLACE,backupWorkRequest)
		}

		every {
			mockWorker.getWorkInfosForUniqueWorkFlow("backup-worker")
		} returns flow { emit(listOf(mockWorkInfo)) }

		viewModel.backup()
		advanceUntilIdle()

		assert(viewModel.backupState.value.inProgress)
		assert(viewModel.backupState.value.progress == 0.5)
		assert(viewModel.backupState.value.operation == "Backing up")
	}

	@Test
	fun `getFiles should update files list`() = testScope.runTest {
		val testFiles = listOf(
			DriveFile("111","backup1",11L,123456L),
			DriveFile("222","backup2",22L,123456L)
		)

		coEvery {
			mockRepository.getFiles()
		} returns flow { emit(Result.success(testFiles)) }

		viewModel.getFiles()
		advanceUntilIdle()

		assert(viewModel.backupState.value.files?.size ==  2)
		assert(viewModel.backupState.value.files?.get(0)?.name == "backup1")
	}

}