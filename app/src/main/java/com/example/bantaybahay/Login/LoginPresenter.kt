package com.example.bantaybahay.Login

class LoginPresenter(
    private val userRepository: UserRepository
) : ILoginPresenter {

    private var view: ILoginView? = null

    override fun attachView(view: ILoginView) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun login(email: String, password: String) {
        view?.showProgress()

        userRepository.loginUser(
            email = email,
            password = password,
            onSuccess = { user ->
                view?.hideProgress()
                view?.onLoginSuccess("Welcome back, ${user.displayName ?: "User"}!")
            },
            onFailure = { errorMessage ->
                view?.hideProgress()
                view?.onLoginFailed(errorMessage)
            }
        )
    }
}