package com.squant.cheetah.domain

sealed class OrderDirection

case object LONG extends OrderDirection //开多单

case object SHORT extends OrderDirection  //开空单
