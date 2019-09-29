package com.wusy.serialportproject.app

import android.util.Log
import android.view.MotionEvent
import com.wusy.wusylibrary.base.BaseActivity
import java.util.*

abstract class BaseTouchActivity : BaseActivity(){
    private var downX: Float=0.0f
    private var downY: Float=0.0f
    private var upX:Float=0.0f
    private var upY:Float=0.0f
    private var distance=250
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event!!.action){
            MotionEvent.ACTION_DOWN->{
                downX=event.rawX
                downY=event.rawY
            }
            MotionEvent.ACTION_MOVE->{

            }
            MotionEvent.ACTION_UP->{
                upX=event.rawX
                upY=event.rawY
                if(upX>downX&&Math.abs(upX-downX)>distance&&Math.abs(upX-downX)>Math.abs(upY-downY)){//右滑
                    Log.i("wsy","用户右滑了")
                    onFingerRightTouch()
                }
                if(upX<downX&&Math.abs(upX-downX)>distance&&Math.abs(upX-downX)>Math.abs(upY-downY)){//左滑
                    Log.i("wsy","用户左滑了")
                    onFingerLeftTouch()
                }
                if(upY<downY&&Math.abs(upY-downY)>distance&&Math.abs(upY-downY)>Math.abs(upX-downX)){//上滑
                    Log.i("wsy","用户上滑了")
                    onFingerTopTouch()
                }
                if(upY>downY&&Math.abs(upY-downY)>distance&&Math.abs(upY-downY)>Math.abs(upX-downX)){//下滑
                    Log.i("wsy","用户下滑了")
                    onFingerBottomTouch()
                }
            }

        }
        return super.onTouchEvent(event)
    }
    open fun onFingerLeftTouch(){

    }
    open fun onFingerRightTouch(){

    }
    open fun onFingerTopTouch(){

    }
    open fun onFingerBottomTouch(){

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Constants.lastUpdateTime= Date(System.currentTimeMillis())
        return super.dispatchTouchEvent(ev)
    }

}
