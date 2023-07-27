package semir.mahovkic.mahala.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import semir.mahovkic.mahala.data.model.Group
import javax.inject.Inject

class GroupsRemoteDataSource @Inject constructor(
    private val api: VotingApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val _groups = mutableListOf<Group>()

    suspend fun getGroupsStream(): Flow<List<Group>> =
        withContext(ioDispatcher) {
            val resp = api.getGroups()
            _groups.clear()
            _groups.addAll(resp)
            MutableStateFlow(_groups)
        }
}