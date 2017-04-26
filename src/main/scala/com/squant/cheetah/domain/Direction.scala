package com.squant.cheetah.domain

sealed class Direction

case object LONG extends Direction //开多单

case object SHORT extends Direction  //开空单
