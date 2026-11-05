package com.example.backend.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;

import java.util.regex.Pattern;

/**
 * JSON Extractor
 */
public class JsonExtractor {

    private final JsonNode rootNode;

    public JsonExtractor(String jsonString) {
        this.rootNode = JsonUtility.deserialize(jsonString);
    }

    /**
     * 获取节点，失败返回 MissingNode，多级路径以 . 拼接
     */
    public JsonNode getNode(String path) {
        if(ValueUtility.isBlank(path))
            return MissingNode.getInstance();

        String[] paths = path.split(Pattern.quote("."));
        if (paths.length == 0)
            return MissingNode.getInstance();

        JsonNode currentNode = this.rootNode;
        for (String p : paths) {
            currentNode = currentNode.path(p);
            if (currentNode.isMissingNode())
                return MissingNode.getInstance();
        }
        return currentNode;
    }

    /**
     * 获取节点，失败抛出异常，多级路径以 . 拼接
     */
    public JsonNode getNodeOrThrow(String path) {
        JsonNode node = getNode(path);
        if (node.isMissingNode()) {
            throw new UtilityException("JSON 中 %s 不存在".formatted(path));
        }
        return node;
    }

    /**
     * 获取节点，失败抛出指定消息异常，多级路径以 . 拼接
     */
    public JsonNode getNodeOrThrow(String path, String message) {
        JsonNode node = getNode(path);
        if (node.isMissingNode()) {
            throw new UtilityException(message);
        }
        return node;
    }

    /**
     * 获取字符串值，失败返回 null，多级路径以 . 拼接
     */
    public String getString(String path) {
        JsonNode node = getNode(path);
        if (node.isMissingNode() || node.isNull())
            return null;
        if(node.isValueNode())
            return node.asText();
        return node.toString();
    }

    /**
     * 获取字符串值，如果为空字符串抛出异常，多级路径以 . 拼接
     */
    public String getNotBlankStringOrThrow(String path) {
        String value = getString(path);
        if(ValueUtility.isBlank(value))
            throw new UtilityException("JSON 中 %s 为空字符串".formatted(path));
        return value;
    }

    /**
     * 获取字符串值，如果为空字符串抛出指定消息异常，多级路径以 . 拼接
     */
    public String getNotBlankStringOrThrow(String path, String message) {
        String value = getString(path);
        if(ValueUtility.isBlank(value))
            throw new UtilityException(message);
        return value;
    }

    /**
     * 获取字符串值，失败返回空字符串，多级路径以 . 拼接
     */
    public String getStringOrEmpty(String path) {
        String value = getString(path);
        if(value == null)
            return "";
        return value;
    }
}