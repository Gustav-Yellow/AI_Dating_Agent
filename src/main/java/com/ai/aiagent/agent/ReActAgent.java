package com.ai.aiagent.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * ReAct(Reasoning-Action-Control) 模式的代理抽象类
 * 实现了思考-行动-控制的循环
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class  ReActAgent extends BaseAgent {

    /**
     * 处理当前状态并决定下一步的运行
     *
     * @return 是否需要执行行动，true表示需要执行行动，false表示不需要执行行动
     */
    public abstract boolean think();

    /**
     * 执行决定的行动
     *
     * @return 行动的执行结果
     */
    public abstract String act();


    /**
     * 执行耽搁步骤：思考和行动
     *
     * @return 步骤执行结果
     */
    @Override
    public String step() {
        try {
            // 先思考
            boolean shouldAct = think();
            if (!shouldAct) {
                return "思考完成 - 无需行动";
            }
            // 再执行
            return act();
        } catch (Exception e) {
            // 记录异常日志
            e.printStackTrace();
            return "步骤执行失败 " + e.getMessage();
        }

    }
}
