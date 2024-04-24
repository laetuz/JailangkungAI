package id.neotica.smartreply

import id.neotica.smartreply.presentation.ChatViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModules = module {
    viewModelOf(::ChatViewModel)
}