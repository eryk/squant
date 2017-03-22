package com.squant.cheetah.domain

import java.time.LocalDateTime

case class StockMoneyFlow(
                           date: LocalDateTime,         //0   日期
                           close: Float,                //1   收盘价
                           change: Float,               //2   涨跌幅
                           turnover: Float,             //3   换手率
                           inflowAmount: Float,         //4   资金流入（万元）
                           outflowAmount: Float,        //5   资金流出（万元）
                           netInflowAmount: Float,      //6   净流入（万元）
                           mainInflowAmount: Float,     //7   主力流入（万元）
                           mainOutflowAmount: Float,    //8   主力流出（万元）
                           mainNetInflowAmount: Float   //9   主力净流入（万元）
                         )