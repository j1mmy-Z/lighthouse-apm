package com.jimmy.lighthouse.apm.agent.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jimmy.lighthouse.apm.agent.LightHouseAgent;
import com.jimmy.lighthouse.apm.agent.plugin.core.PluginSearcher;
import com.jimmy.lighthouse.apm.common.exception.PluginException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-05
 */
@Slf4j
public class ConfigLoader {

    private static final String CONFIG_PATH_ARG = "lighthouse.config.path";

    private static final String DEFAULT_CONFIG_PATH = "/config/lighthouse-agent.config";

    private static final String SYSTEM_CONFIG_PREFIX = "lighthouse.";

    public static void loadAgentArgs(String agentArgs) throws IllegalAccessException {

        // 1.配置文件
        String configPath = System.getProperty(CONFIG_PATH_ARG);
        File configFile = StringUtils.isEmpty(configPath) ?
                new File(PluginSearcher.findPath(), DEFAULT_CONFIG_PATH)
                : new File(configPath);
        if (!configFile.exists() || !configFile.isFile()) {
            throw new PluginException("lighthouse-agent.config not found");
        }
        InputStreamReader inputStreamReader = null;
        Properties properties = new Properties();
        try {
            // 替换占位符
            convertPlaceholders(properties);
            // 格式化key
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                Object value = properties.remove(entry.getKey());
                properties.put(formatConfigKey(entry.getKey().toString()), value);
            }
            inputStreamReader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8);
            properties.load(inputStreamReader);
        } catch (Exception e) {
            log.error("fail to load config properties", e);
        } finally {
            try {
                if (Objects.nonNull(inputStreamReader))
                    inputStreamReader.close();
            } catch (IOException ignored) {
            }
        }

        // 2. 环境变量
        Properties systemProps = System.getProperties();
        for (Map.Entry<Object, Object> entry : systemProps.entrySet()) {
            String propName = entry.getKey().toString();
            if (StringUtils.startsWith(propName, SYSTEM_CONFIG_PREFIX)) {
                String key = propName.substring(SYSTEM_CONFIG_PREFIX.length());
                key = formatConfigKey(key);
                properties.put(key, entry.getValue());
            }
        }

        // 3.agent参数,以逗号分隔
        agentArgs = StringUtils.trim(agentArgs);

        if (StringUtils.isNotBlank(agentArgs)) {
            String[] args = agentArgs.split(",");
            for (String arg : args) {
                String[] pair = arg.split("=");
                if (pair.length != 2) {
                    throw new PluginException("agentArgs " + arg + " is not split with '='");
                }
                properties.put(formatConfigKey(pair[0]), pair[1]);
            }
        }

        // 4.将配置项赋值到配置文件中
        Field[] fields = LightHouseAgent.class.getFields();
        for (Field field : fields) {
            if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                String configKey = field.getName().toLowerCase();
                String configValue = properties.getProperty(configKey);
                Object value = convertType(field.getType(), configValue);
                if (Objects.nonNull(value)) {
                    field.set(null, value);
                }
            }
        }
    }

    /**
     * 格式化key
     */
    private static String formatConfigKey(String key) {
        return key.replaceAll("-", "").replaceAll("\\.", "")
                .replaceAll("_", "");
    }

    /**
     * 替换配置文件中的占位符${}
     */
    private static void convertPlaceholders(Properties properties) {
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String valueStr = entry.getValue().toString();
            if (StringUtils.isBlank(valueStr)) {
                continue;
            }
            HashMap<String, Object> map = Maps.newHashMap();
            String key;
            char[] charArray = valueStr.toCharArray();
            int start = -1;
            int end;
            for (int i = 0; i < charArray.length; i++) {
                if (charArray[i] == '$') {
                    if (i < charArray.length - 3 && charArray[i + 1] == '{') {
                        start = ++i;
                    } else {
                        // 异常
                        throw new PluginException("lighthouse-agent.config contains illegal property:" + valueStr);
                    }
                } else if (charArray[i] == '}') {
                    end = i;
                    if (start == -1 || start + 1 >= end) {
                        // 异常
                        throw new PluginException("lighthouse-agent.config contains illegal property:" + valueStr);
                    }
                    key = StringUtils.substring(valueStr, start + 1, end);
                    Object val = properties.get(key);
                    if (Objects.isNull(val)) {
                        throw new PluginException("lighthouse-agent.config no property matches placeholder:" + key);
                    }
                    map.put(key, val);
                    start = -1;
                }
            }
            if (!map.isEmpty()) {
                String newValue = StringSubstitutor.replace(valueStr, map);
                // 占位符不完整
                if (newValue.contains("$") || newValue.contains("{") || newValue.contains("}")) {
                    throw new PluginException("lighthouse-agent.config contains illegal property:" + valueStr);
                }
                properties.replace(entry.getKey(), newValue);
            }
        }

    }

    private static Object convertType(Class<?> type, String configValue) {
        if (Objects.isNull(type) || Objects.isNull(configValue)) {
            return null;
        }

        Object value = null;
        if (String.class.equals(type)) {
            value = configValue;
        } else if (int.class.equals(type) || Integer.class.equals(type)) {
            value = Integer.valueOf(configValue);
        } else if (long.class.equals(type) || Long.class.equals(type)) {
            value = Long.parseLong(configValue);
        } else if (boolean.class.equals(type) || Boolean.class.equals(type)) {
            value = Boolean.valueOf(configValue);
        } else if (float.class.equals(type) || Float.class.equals(type)) {
            value = Float.parseFloat(configValue);
        } else if (double.class.equals(type) || Double.class.equals(type)) {
            value = Double.valueOf(configValue);
        } else if (List.class.equals(type)) {
            if (StringUtils.isEmpty(configValue)) {
                value = CollectionUtils.emptyCollection();
            } else {
                String[] parts = configValue.split(",");
                ArrayList<String> list = Lists.newArrayList();
                for (String part : parts) {
                    part = part.trim();
                    if (StringUtils.isEmpty(part)) {
                        list.add(part);
                    }
                }
                value = list;
            }
        }
        return value;
    }
}
