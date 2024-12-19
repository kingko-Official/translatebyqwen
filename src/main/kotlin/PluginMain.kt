package org.example.mirai.plugin

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import com.alibaba.dashscope.aigc.generation.Generation
import com.alibaba.dashscope.aigc.generation.GenerationParam
import com.alibaba.dashscope.aigc.generation.GenerationResult
import com.alibaba.dashscope.common.Message
import com.alibaba.dashscope.common.Role
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.permission.PermissionService
import net.mamoe.mirai.console.permission.AbstractPermitteeId
import net.mamoe.mirai.console.permission.PermissionService.Companion.hasPermission
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.utils.error
import net.mamoe.mirai.utils.info
import org.example.mirai.plugin.ApiKeyConfig.apiKey

object ApiKeyConfig : AutoSavePluginConfig("ApiKeyConfig") {

    var apiKey by value("请输入apikey") // 存储 API key
}


object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "org.tranbyqwen.mirai",
        name = "翻译插件",
        version = "0.1.0"
    ) {
        author("kingko")
        info(
            """
            这是一个翻译插件
        """.trimIndent()
        )
    }
) {



    override fun onEnable() {
        logger.info { "插件已启用" }

        ApiKeyConfig.reload()

        // 监听群消息，处理翻译指令
        val eventChannel = GlobalEventChannel.parentScope(this)
        eventChannel.subscribeAlways<net.mamoe.mirai.event.events.GroupMessageEvent> {
            // 检查消息是否是 /翻译 开头
            if (message.contentToString().startsWith("/翻译")) {
                val text = message.contentToString().removePrefix("/翻译").trim()
                if (text.isNotEmpty()) {
                    val translatedText = translateText(text)
                    group.sendMessage("翻译结果: $translatedText")
                } else {
                    group.sendMessage("请提供需要翻译的文本！")
                }
            }
        }

    }



    // 调用通义千问进行翻译
    private fun translateText(text: String): String {
        val gen = Generation()

        // 构造消息
        val systemMsg = Message.builder()
            .role(Role.SYSTEM.value)
            .content("You are a helpful assistant that translates text into Chinese.")
            .build()

        val userMsg = Message.builder()
            .role(Role.USER.value)
            .content("Translate the following text to Chinese: $text")
            .build()

        val param = GenerationParam.builder()
            .apiKey(apiKey) // 使用读取的 API Key
            .model("qwen-plus") // 使用通义千问模型
            .messages(listOf(systemMsg, userMsg))
            .resultFormat(GenerationParam.ResultFormat.MESSAGE)
            .build()

        // 调用 API
        val result: GenerationResult = gen.call(param)

        // 获取翻译结果
        val translatedText = result.output?.choices?.get(0)?.message?.content

        // 返回翻译结果
        return translatedText ?: "未能获取翻译结果"
    }
}
