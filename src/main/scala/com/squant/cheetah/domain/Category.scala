package com.squant.cheetah.domain

case class Category(name: String, code: String, categoryType: String, pinyin: String, url: String, stocks: List[String])
