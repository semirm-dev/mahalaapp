package semir.mahovkic.mahala.ui.candidate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import semir.mahovkic.mahala.data.Candidate
import semir.mahovkic.mahala.data.CandidatesRepository

class CandidatesViewModel(
    private val candidatesRepository: CandidatesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CandidatesUiState())
    val uiState: StateFlow<CandidatesUiState> = _state

    init {
        viewModelScope.launch {
            candidatesRepository.getCandidates().collect {
                _state.value = CandidatesUiState(it.map { candidate -> candidate.toUiState() }, "")
            }
        }
    }

    fun vote(candidateId: Int) {
        candidatesRepository.incrementVote(candidateId)?.let { updatedCandidate ->
            val updated = CandidatesUiState(_state.value.candidatesUiState.also { currentState ->
                currentState.find { currentCandidate -> currentCandidate.id == updatedCandidate.id }
                    ?.also {
                        it.votes = updatedCandidate.votes
                    }
            }, "last candidate ${updatedCandidate.id} - total votes: ${updatedCandidate.votes}")

            _state.value = updated
        }
    }
}

data class CandidatesUiState(
    val candidatesUiState: List<CandidateUiState> = listOf(),
    val message: String = ""
)

data class CandidateUiState(
    val id: Int,
    val name: String,
    val profileImg: Int,
    val party: String,
    var votes: Int
)

fun Candidate.toUiState(): CandidateUiState = CandidateUiState(
    id = id,
    name = name,
    profileImg = profileImg,
    party = party,
    votes = votes
)
