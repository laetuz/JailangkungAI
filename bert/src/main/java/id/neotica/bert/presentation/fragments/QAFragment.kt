package id.neotica.bert.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import id.neotica.bert.R
import id.neotica.bert.data.BertQAHelper
import id.neotica.bert.data.DataSetClient
import id.neotica.bert.data.models.Message
import id.neotica.bert.databinding.FragmentQABinding
import id.neotica.bert.presentation.adapters.ChatHistoryAdapter
import id.neotica.bert.presentation.adapters.QuestionSuggestionsAdapter
import org.tensorflow.lite.task.text.qa.QaAnswer

class QAFragment : Fragment(R.layout.fragment_q_a) {
    private var _binding: FragmentQABinding? = null
    private val binding: FragmentQABinding get() = _binding!!

    private lateinit var chatAdapter: ChatHistoryAdapter
    private lateinit var bertQAHelper: BertQAHelper

    private val args: QAFragmentArgs by navArgs()
    private var topicContent: String = ""
    private var topicSuggestedQuestions: List<String> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentQABinding.bind(view)

        binding.topAppBar.title = args.topicTitle

        setupUI()
        initChatHistoryRecView()

        initChatHistoryRecView()
        initQuestionSuggestionsRecView()
        initBertQAModel()
    }

    private fun setupUI() {
        with(binding) {

            val client = DataSetClient(requireActivity())
            client.loadJsonData()?.let {
                topicContent = it.getContents()[args.topicId]
                topicSuggestedQuestions = it.questions[args.topicId]
            }

            tietQuestion.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val shouldSendButtonActive: Boolean = s.isNullOrEmpty()
                    ibSend.isClickable = !shouldSendButtonActive
                }

                override fun afterTextChanged(s: Editable?) {
                    //
                }

            })

            ibSend.setOnClickListener {
                if (it.isClickable && (tietQuestion.text?.isNotEmpty() == true)) {
                    with(tietQuestion) {
                        progressBar.visibility = View.VISIBLE

                        val question = this.text.toString()
                        this.text?.clear()

                        chatAdapter.addMessage(Message(question, true))
                        Handler(Looper.getMainLooper()).post {
                            bertQAHelper.getQuestionAnswerer(topicContent, question)
                            progressBar.visibility = View.GONE
                        }
                    }
                } else {
                    Toast.makeText(context, "Insert question first.", Toast.LENGTH_SHORT).show()
                }

                val imm = requireContext().getSystemService(
                    Context.INPUT_METHOD_SERVICE
                ) as InputMethodManager

                imm.hideSoftInputFromWindow(it.windowToken, 0)
            }

            tvInferenceTime.text = String.format(
                requireContext().getString(R.string.tv_inference_time_label),
                0L
            )
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
    }

    private fun initChatHistoryRecView() {
        val historyLayoutManager = LinearLayoutManager(context)
        binding.rvChatHistory.layoutManager = historyLayoutManager

        chatAdapter = ChatHistoryAdapter()
        binding.rvChatHistory.adapter = chatAdapter

        chatAdapter.addMessage(Message(topicContent, false))
    }

    private fun initQuestionSuggestionsRecView() {
        with(binding) {
            if (topicSuggestedQuestions.isNotEmpty()) {
                val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
                with(rvQuestionSuggestions) {
                    adapter = QuestionSuggestionsAdapter(
                        topicSuggestedQuestions,
                        object : QuestionSuggestionsAdapter.OnOptionClicked {
                            override fun onOptionClicked(optionID: Int) {
                                setQuestion(optionID)
                            }
                        }
                    )
                    layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    addItemDecoration(decoration)
                }
            } else {
                tvSuggestion.visibility = View.GONE
                rvQuestionSuggestions.visibility = View.GONE
            }
        }
    }

    private fun initBertQAModel() {
        bertQAHelper = BertQAHelper(requireContext(), object: BertQAHelper.ResultAnswerListener {
            override fun onError(error: String) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }

            override fun onResults(result: List<QaAnswer>?, inferenceTime: Long) {
                result?.first()?.let {
                    chatAdapter.addMessage(Message(it.text, false))
                    binding.rvChatHistory.scrollToPosition(chatAdapter.itemCount -1)
                    binding.tvInferenceTime.text = String.format(
                        requireContext().getString(R.string.tv_inference_time_label),
                        inferenceTime
                    )
                }
            }

        })
    }

    private fun setQuestion(position: Int) {
        binding.tietQuestion.setText(
            topicSuggestedQuestions[position]
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.tietQuestion.addTextChangedListener(null)
        bertQAHelper.clearBert()
        _binding = null
    }
}