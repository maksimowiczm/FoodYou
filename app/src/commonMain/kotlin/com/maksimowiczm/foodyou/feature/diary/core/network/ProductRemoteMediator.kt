package com.maksimowiczm.foodyou.feature.diary.core.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator

// You probably wonder why tf is this generic but you should probably wonder why paging library
// enforces you to define type for the remote mediator.
//
// Probably they know why... But I don't.
//
// Product mediator must not have any type restrictions because it will be used with custom search
// entities which will allow for searching for products together with recipes and other entities
// (all in one paging source).
@OptIn(ExperimentalPagingApi::class)
internal typealias ProductRemoteMediator<T> = RemoteMediator<Int, T>
