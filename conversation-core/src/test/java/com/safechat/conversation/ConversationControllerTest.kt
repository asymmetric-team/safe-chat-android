package com.safechat.conversation

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.Observable.just

class ConversationControllerTest {

    val cipher = mock<ConversationCipher>()
    val repository = mock<ConversationRepository>()
    val service = mock<ConversationService>()
    val controller = ConversationControllerImpl(service, repository, cipher)

    val encryptedMessages = listOf(Message("encrypted_text", false))
    val decryptedMessages = listOf(Message("decrypted_text", false))

    @Before
    fun setUp() {
        stubRepository()
        stubService(just(encryptedMessages))
        stubCipher(just(decryptedMessages))
    }

    @Test
    fun shouldReadSavedPublicKey() {
        startController()
        verify(repository).getPublicKeyString()
    }

    @Test
    fun shouldDownloadPreviousMessages() {
        startController()
        verify(service).getPreviousMessages("myPublicKey", "otherPublicKey")
    }

    @Test
    fun shouldDecryptMessages() {
        startController()
        verify(cipher).decryptMessages("symmetricKey", encryptedMessages)
    }

    private fun startController() {
        controller.onCreated("otherPublicKey")
    }

    private fun stubRepository() {
        whenever(repository.getPublicKeyString()).thenReturn("myPublicKey")
        whenever(repository.getDecryptedSymmetricKey("otherPublicKey")).thenReturn("symmetricKey")
    }

    private fun stubService(messages: Observable<List<Message>>) {
        whenever(service.getPreviousMessages("myPublicKey", "otherPublicKey")).thenReturn(messages)
    }

    private fun stubCipher(messages: Observable<List<Message>>) {
        whenever(cipher.decryptMessages("symmetricKey", encryptedMessages)).thenReturn(messages)
    }
}