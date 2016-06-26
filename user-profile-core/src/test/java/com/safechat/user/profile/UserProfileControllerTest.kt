package com.safechat.user.profile

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import rx.Observable

class UserProfileControllerTest {

    val service = mock<UsersService>()
    val repository = mock<UserProfileRepository>()
    val suidCalculator = mock<SuidCalculator>()
    val view = mock<UserProfileView>()
    val controller = UserProfileController(service, repository, suidCalculator, view)

    @Before
    fun setUp() {
        whenever(suidCalculator.findShortestUniqueSubstring(any(), any())).thenReturn("my_public")
        whenever(service.getUsers()).thenReturn(Observable.just(listOf(User("A"), User("B"))))
        whenever(repository.getPublicKeyString()).thenReturn("my_public_key")
    }

    @Test
    fun shouldGetAllUsersOnCreate() {
        controller.onCreate()

        verify(service).getUsers()
    }

    @Test
    fun shouldGetOwnerKeyFromRepositoryOnCreate() {
        controller.onCreate()

        verify(repository).getPublicKeyString()
    }


    @Test
    fun shouldShowShortestUniqueSubstringOfUserRSA() {
        controller.onCreate()

        verify(view).showShortestUniqueId(Mockito.anyString())
    }
}

interface UserProfileView {
    fun showShortestUniqueId(suid: String)

}

interface UserProfileRepository {
    fun getPublicKeyString(): String

}

interface UsersService {
    fun getUsers(): Observable<List<User>>
}

class UserProfileController(val service: UsersService,
                            val repository: UserProfileRepository,
                            val suidCalculator: SuidCalculator,
                            val view: UserProfileView) {
    fun onCreate() {
        val myPublicKey = repository.getPublicKeyString()
        service.getUsers()
                .map { it.filterNot { it.rsa == myPublicKey } }
                .map { suidCalculator.findShortestUniqueSubstring(myPublicKey, it.map { it.rsa }) }
                .subscribe(onSuccess, {})
    }

    val onSuccess: (String) -> Unit = {
        view.showShortestUniqueId(it)
    }
}