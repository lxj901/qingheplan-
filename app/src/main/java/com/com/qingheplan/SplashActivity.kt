package com.com.qingheplan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*

/**
 * 开屏页面 - 完全复刻 SwiftUI 版本
 * 包含渐变背景、装饰元素动画、Logo动画、文字动画
 * 以及 ATT 和推送通知权限请求流程
 */
class SplashActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SplashView"
    }

    // 协程作用域
    private val activityScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // 推送通知权限请求
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        println("📊 [$TAG] 推送通知权限请求结果: ${if (isGranted) "已授权" else "已拒绝"}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 隐藏ActionBar
        supportActionBar?.hide()

        // 启动动画
        startAnimations()

        // 请求权限并加载
        requestPermissionsAndLoad()
    }

    /**
     * 启动所有动画效果
     */
    private fun startAnimations() {
        val logoContainer = findViewById<View>(R.id.logoContainer)
        val textContainer = findViewById<View>(R.id.textContainer)
        val versionInfo = findViewById<View>(R.id.versionInfo)
        val decorationTopLeft = findViewById<View>(R.id.decorationCircleTopLeft)
        val decorationBottomRight = findViewById<View>(R.id.decorationCircleBottomRight)
        val decorationMiddle = findViewById<View>(R.id.decorationCircleMiddle)

        // 初始状态设置
        logoContainer.alpha = 0f
        logoContainer.scaleX = 0.8f
        logoContainer.scaleY = 0.8f
        textContainer.alpha = 0f
        versionInfo.alpha = 0f

        // 1. Logo 动画 - 延迟 300ms，弹簧效果
        logoContainer.postDelayed({
            logoContainer.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(800)
                .setInterpolator(SpringInterpolator(0.6f))
                .start()
        }, 300)

        // 2. 文字动画 - 延迟 600ms
        textContainer.postDelayed({
            textContainer.animate()
                .alpha(1f)
                .setDuration(600)
                .start()
        }, 600)

        versionInfo.postDelayed({
            versionInfo.animate()
                .alpha(1f)
                .setDuration(600)
                .start()
        }, 600)

        // 3. 装饰元素呼吸动画 - 延迟 1000ms，无限循环
        decorationTopLeft.postDelayed({
            startBreathingAnimation(decorationTopLeft, 1.0f, 1.2f)
        }, 1000)

        decorationBottomRight.postDelayed({
            startBreathingAnimation(decorationBottomRight, 1.0f, 1.1f)
        }, 1000)

        decorationMiddle.postDelayed({
            startBreathingAnimation(decorationMiddle, 1.0f, 1.3f)
        }, 1000)
    }

    /**
     * 呼吸动画 - 缩放循环
     */
    private fun startBreathingAnimation(view: View, fromScale: Float, toScale: Float) {
        val scaleAnimation = ScaleAnimation(
            fromScale, toScale,
            fromScale, toScale,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 2000
            repeatCount = Animation.INFINITE
            repeatMode = Animation.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
        }
        view.startAnimation(scaleAnimation)
    }

    /**
     * 请求权限并加载 - 复刻 SwiftUI 流程
     */
    private fun requestPermissionsAndLoad() {
        activityScope.launch {
            println("📊 [$TAG] 启动页加载，开始权限请求流程")

            // 延迟 1 秒，确保 UI 完全加载
            delay(1000)

            // 第一步：请求 ATT 权限（Android 上对应广告 ID 权限）
            println("📊 [$TAG] 🎯 第 1 步：请求 ATT 权限（Android 广告 ID）")
            requestATTPermission()
            println("📊 [$TAG] ✅ ATT 权限请求完成")

            // 延迟 0.5 秒
            delay(500)

            // 第二步：请求推送通知权限（仅 Android 13+ 需要）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val hasPermission = ContextCompat.checkSelfPermission(
                    this@SplashActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

                if (!hasPermission) {
                    println("📊 [$TAG] 🎯 第 2 步：请求推送通知权限")
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    delay(500) // 等待用户响应
                    println("📊 [$TAG] ✅ 推送通知权限请求完成")
                } else {
                    println("📊 [$TAG] ℹ️ 推送权限状态：已授权，跳过请求")
                }
            } else {
                println("📊 [$TAG] ℹ️ Android 版本 < 13，无需请求推送权限")
            }

            // 完成启动页
            println("📊 [$TAG] ✅ ATT+推送权限流程完成，进入下一步")
            delay(500)
            startMainActivity()
        }
    }

    /**
     * 请求 ATT 权限（Android 上的广告 ID 访问）
     */
    private fun requestATTPermission() {
        // Android 上没有直接对应的 ATT，但可以记录日志
        // 实际应用中可以在这里初始化广告 SDK
        println("📊 [$TAG] Android 平台：广告追踪权限（对应 iOS ATT）")
    }

    /**
     * 跳转到主页面
     */
    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        // 添加淡入淡出动画效果
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancel()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // 在开屏页面禁用返回键
    }

    /**
     * 弹簧插值器 - 模拟 SwiftUI 的 spring 动画
     */
    private class SpringInterpolator(private val dampingFraction: Float) : AccelerateDecelerateInterpolator() {
        override fun getInterpolation(input: Float): Float {
            // 简化的弹簧效果
            val overshoot = 1.0f - dampingFraction
            return if (input < 0.5f) {
                super.getInterpolation(input * 2) * (1 + overshoot) / 2
            } else {
                1 - (1 - super.getInterpolation((input - 0.5f) * 2)) * overshoot / 2
            }
        }
    }
}

