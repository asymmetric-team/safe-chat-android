package com.safechat.conversation.select

class ConversationsListControllerImpl(
        val repository: ConversationsListRepository,
        val view: ConversationsListView) : ConversationsListController {

    override fun onResume() {
        val conversations = repository.getConversationsMessages()
        if (conversations.isNotEmpty()) {
            view.showConversations(conversations)
        } else {
            view.showEmptyConversationsPlaceholder()
        }
    }
}