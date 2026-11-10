package com.example.backend.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON Extractor
 */
public class JsonExtractor {

    private final JsonNode rootNode;

    public JsonExtractor(String jsonString) {
        this.rootNode = JsonUtility.deserialize(jsonString);
    }

    /**
     * 获取节点，失败返回 MissingNode
     */
    public JsonNode getNode(String name) {
        if (ValueUtility.isBlank(name))
            return MissingNode.getInstance();
        return rootNode.path(name);
    }

    /**
     * 获取节点，失败抛出指定消息异常
     */
    public JsonNode getNodeOrThrow(String name, String message) {
        JsonNode node = getNode(name);
        if (node.isMissingNode()) {
            throw new UtilityException(message);
        }
        return node;
    }

    /**
     * 获取节点，失败抛出异常
     */
    public JsonNode getNodeOrThrow(String name) {
        return getNodeOrThrow(name, "JSON 中不存在 %s".formatted(name));
    }

    /**
     * 获取节点，失败返回 MissingNode
     */
    public JsonNode getNode(List<String> names) {
        if (names == null || names.isEmpty())
            return MissingNode.getInstance();
        JsonNode node = rootNode;
        for (String name : names) {
            node = node.path(name);
            if (node.isMissingNode())
                return MissingNode.getInstance();
        }
        return node;
    }

    /**
     * 获取节点，失败抛出指定消息异常
     */
    public JsonNode getNodeOrThrow(List<String> names, String message) {
        JsonNode node = getNode(names);
        if (node.isMissingNode()) {
            throw new UtilityException(message);
        }
        return node;
    }

    /**
     * 获取节点，失败抛出异常
     */
    public JsonNode getNodeOrThrow(List<String> names) {
        return getNodeOrThrow(names, "JSON 中不存在 %s".formatted(String.join(".", names)));
    }

    // =================================================================================================================

    /**
     * 获取字符串值，失败返回 null
     */
    public String getStringOrNull(String name) {
        JsonNode node = getNode(name);
        if (node.isMissingNode() || node.isNull())
            return null;
        if (node.isValueNode())
            return node.asText();
        return node.toString();
    }

    /**
     * 获取字符串值，失败返回空字符串
     */
    public String getStringOrEmpty(String name) {
        String value = getStringOrNull(name);
        if (value == null)
            return "";
        return value;
    }

    /**
     * 获取字符串值，如果为空字符串抛出指定消息异常
     */
    public String getNotBlankStringOrThrow(String name, String message) {
        String value = getStringOrNull(name);
        if (ValueUtility.isBlank(value))
            throw new UtilityException(message);
        return value;
    }

    /**
     * 获取字符串值，如果为空字符串抛出异常
     */
    public String getNotBlankStringOrThrow(String name) {
        return getNotBlankStringOrThrow(name, "JSON 中 %s 为空字符串".formatted(name));
    }

    /**
     * 获取字符串值，失败返回 null
     */
    public String getStringOrNull(List<String> names) {
        JsonNode node = getNode(names);
        if (node.isMissingNode() || node.isNull())
            return null;
        if (node.isValueNode())
            return node.asText();
        return node.toString();
    }

    /**
     * 获取字符串值，失败返回空字符串
     */
    public String getStringOrEmpty(List<String> names) {
        String value = getStringOrNull(names);
        if (value == null)
            return "";
        return value;
    }

    /**
     * 获取字符串值，如果为空字符串抛出指定消息异常
     */
    public String getNotBlankStringOrThrow(List<String> names, String message) {
        String value = getStringOrNull(names);
        if (ValueUtility.isBlank(value))
            throw new UtilityException(message);
        return value;
    }

    /**
     * 获取字符串值，如果为空字符串抛出异常
     */
    public String getNotBlankStringOrThrow(List<String> names) {
        return getNotBlankStringOrThrow(names, "JSON 中 %s 为空字符串".formatted(String.join(".", names)));
    }

    // =================================================================================================================

    /**
     * 获取字符串列表
     */
    public List<String> getStringList(String name) {
        JsonNode node = getNode(name);
        List<String> list = new ArrayList<>();
        if (node.isMissingNode() || !node.isArray())
            return list;
        for (JsonNode n : node) {
            list.add(n.asText().trim());
        }
        return list;
    }

    /**
     * 获取字符串列表
     */
    public List<String> getStringList(List<String> names) {
        JsonNode node = getNode(names);
        List<String> list = new ArrayList<>();
        if (node.isMissingNode() || !node.isArray())
            return list;
        for (JsonNode n : node) {
            list.add(n.asText().trim());
        }
        return list;
    }

    // =================================================================================================================

    public Boolean getBoolOrNull(String name) {
        return ValueUtility.toBoolOrNull(getStringOrNull(name));
    }

    public boolean getBoolOrThrow(String name, String message) {
        return ValueUtility.toBoolOrThrow(getStringOrNull(name), message);
    }

    public boolean getBoolOrThrow(String name) {
        return ValueUtility.toBoolOrThrow(getStringOrNull(name));
    }

    public Boolean getBoolAllowBlankOrThrow(String name, String message) {
        return ValueUtility.toBoolAllowBlankOrThrow(getStringOrNull(name), message);
    }

    public Boolean getBoolAllowBlankOrThrow(String name) {
        return ValueUtility.toBoolAllowBlankOrThrow(getStringOrNull(name));
    }

    public Boolean getBoolOrNull(List<String> names) {
        return ValueUtility.toBoolOrNull(getStringOrNull(names));
    }

    public boolean getBoolOrThrow(List<String> names, String message) {
        return ValueUtility.toBoolOrThrow(getStringOrNull(names), message);
    }

    public boolean getBoolOrThrow(List<String> names) {
        return ValueUtility.toBoolOrThrow(getStringOrNull(names));
    }

    public Boolean getBoolAllowBlankOrThrow(List<String> names, String message) {
        return ValueUtility.toBoolAllowBlankOrThrow(getStringOrNull(names), message);
    }

    public Boolean getBoolAllowBlankOrThrow(List<String> names) {
        return ValueUtility.toBoolAllowBlankOrThrow(getStringOrNull(names));
    }

    // =================================================================================================================

    public Integer getIntOrNull(String name) {
        return ValueUtility.toIntOrNull(getStringOrNull(name));
    }

    public int getIntOrThrow(String name, String message) {
        return ValueUtility.toIntOrThrow(getStringOrNull(name), message);
    }

    public int getIntOrThrow(String name) {
        return ValueUtility.toIntOrThrow(getStringOrNull(name));
    }

    public Integer getIntAllowBlankOrThrow(String name, String message) {
        return ValueUtility.toIntAllowBlankOrThrow(getStringOrNull(name), message);
    }

    public Integer getIntAllowBlankOrThrow(String name) {
        return ValueUtility.toIntAllowBlankOrThrow(getStringOrNull(name));
    }

    public Integer getIntOrNull(List<String> names) {
        return ValueUtility.toIntOrNull(getStringOrNull(names));
    }

    public int getIntOrThrow(List<String> names, String message) {
        return ValueUtility.toIntOrThrow(getStringOrNull(names), message);
    }

    public int getIntOrThrow(List<String> names) {
        return ValueUtility.toIntOrThrow(getStringOrNull(names));
    }

    public Integer getIntAllowBlankOrThrow(List<String> names, String message) {
        return ValueUtility.toIntAllowBlankOrThrow(getStringOrNull(names), message);
    }

    public Integer getIntAllowBlankOrThrow(List<String> names) {
        return ValueUtility.toIntAllowBlankOrThrow(getStringOrNull(names));
    }

    // =================================================================================================================

    public Long getLongOrNull(String name) {
        return ValueUtility.toLongOrNull(getStringOrNull(name));
    }

    public long getLongOrThrow(String name, String message) {
        return ValueUtility.toLongOrThrow(getStringOrNull(name), message);
    }

    public long getLongOrThrow(String name) {
        return ValueUtility.toLongOrThrow(getStringOrNull(name));
    }

    public Long getLongAllowBlankOrThrow(String name, String message) {
        return ValueUtility.toLongAllowBlankOrThrow(getStringOrNull(name), message);
    }

    public Long getLongAllowBlankOrThrow(String name) {
        return ValueUtility.toLongAllowBlankOrThrow(getStringOrNull(name));
    }

    public Long getLongOrNull(List<String> names) {
        return ValueUtility.toLongOrNull(getStringOrNull(names));
    }

    public long getLongOrThrow(List<String> names, String message) {
        return ValueUtility.toLongOrThrow(getStringOrNull(names), message);
    }

    public long getLongOrThrow(List<String> names) {
        return ValueUtility.toLongOrThrow(getStringOrNull(names));
    }

    public Long getLongAllowBlankOrThrow(List<String> names, String message) {
        return ValueUtility.toLongAllowBlankOrThrow(getStringOrNull(names), message);
    }

    public Long getLongAllowBlankOrThrow(List<String> names) {
        return ValueUtility.toLongAllowBlankOrThrow(getStringOrNull(names));
    }

    // =================================================================================================================

    public BigDecimal getDecimalOrNull(String name) {
        return ValueUtility.toDecimalOrNull(getStringOrNull(name));
    }

    public BigDecimal getDecimalOrThrow(String name, String message) {
        return ValueUtility.toDecimalOrThrow(getStringOrNull(name), message);
    }

    public BigDecimal getDecimalOrThrow(String name) {
        return ValueUtility.toDecimalOrThrow(getStringOrNull(name));
    }

    public BigDecimal getDecimalAllowBlankOrThrow(String name, String message) {
        return ValueUtility.toDecimalAllowBlankOrThrow(getStringOrNull(name), message);
    }

    public BigDecimal getDecimalAllowBlankOrThrow(String name) {
        return ValueUtility.toDecimalAllowBlankOrThrow(getStringOrNull(name));
    }

    public BigDecimal getDecimalOrNull(List<String> names) {
        return ValueUtility.toDecimalOrNull(getStringOrNull(names));
    }

    public BigDecimal getDecimalOrThrow(List<String> names, String message) {
        return ValueUtility.toDecimalOrThrow(getStringOrNull(names), message);
    }

    public BigDecimal getDecimalOrThrow(List<String> names) {
        return ValueUtility.toDecimalOrThrow(getStringOrNull(names));
    }

    public BigDecimal getDecimalAllowBlankOrThrow(List<String> names, String message) {
        return ValueUtility.toDecimalAllowBlankOrThrow(getStringOrNull(names), message);
    }

    public BigDecimal getDecimalAllowBlankOrThrow(List<String> names) {
        return ValueUtility.toDecimalAllowBlankOrThrow(getStringOrNull(names));
    }

    // =================================================================================================================

    public LocalDateTime getDateTimeOrNull(String name) {
        return ValueUtility.toDateTimeOrNull(getStringOrNull(name));
    }

    public LocalDateTime getDateTimeOrThrow(String name, String message) {
        return ValueUtility.toDateTimeOrThrow(getStringOrNull(name), message);
    }

    public LocalDateTime getDateTimeOrThrow(String name) {
        return ValueUtility.toDateTimeOrThrow(getStringOrNull(name));
    }

    public LocalDateTime getDateTimeAllowBlankOrThrow(String name, String message) {
        return ValueUtility.toDateTimeAllowBlankOrThrow(getStringOrNull(name), message);
    }

    public LocalDateTime getDateTimeAllowBlankOrThrow(String name) {
        return ValueUtility.toDateTimeAllowBlankOrThrow(getStringOrNull(name));
    }

    public LocalDateTime getDateTimeOrNull(List<String> names) {
        return ValueUtility.toDateTimeOrNull(getStringOrNull(names));
    }

    public LocalDateTime getDateTimeOrThrow(List<String> names, String message) {
        return ValueUtility.toDateTimeOrThrow(getStringOrNull(names), message);
    }

    public LocalDateTime getDateTimeOrThrow(List<String> names) {
        return ValueUtility.toDateTimeOrThrow(getStringOrNull(names));
    }

    public LocalDateTime getDateTimeAllowBlankOrThrow(List<String> names, String message) {
        return ValueUtility.toDateTimeAllowBlankOrThrow(getStringOrNull(names), message);
    }

    public LocalDateTime getDateTimeAllowBlankOrThrow(List<String> names) {
        return ValueUtility.toDateTimeAllowBlankOrThrow(getStringOrNull(names));
    }

    // =================================================================================================================

    public LocalDate getDateOrNull(String name) {
        return ValueUtility.toDateOrNull(getStringOrNull(name));
    }

    public LocalDate getDateOrThrow(String name, String message) {
        return ValueUtility.toDateOrThrow(getStringOrNull(name), message);
    }

    public LocalDate getDateOrThrow(String name) {
        return ValueUtility.toDateOrThrow(getStringOrNull(name));
    }

    public LocalDate getDateAllowBlankOrThrow(String name, String message) {
        return ValueUtility.toDateAllowBlankOrThrow(getStringOrNull(name), message);
    }

    public LocalDate getDateAllowBlankOrThrow(String name) {
        return ValueUtility.toDateAllowBlankOrThrow(getStringOrNull(name));
    }

    public LocalDate getDateOrNull(List<String> names) {
        return ValueUtility.toDateOrNull(getStringOrNull(names));
    }

    public LocalDate getDateOrThrow(List<String> names, String message) {
        return ValueUtility.toDateOrThrow(getStringOrNull(names), message);
    }

    public LocalDate getDateOrThrow(List<String> names) {
        return ValueUtility.toDateOrThrow(getStringOrNull(names));
    }

    public LocalDate getDateAllowBlankOrThrow(List<String> names, String message) {
        return ValueUtility.toDateAllowBlankOrThrow(getStringOrNull(names), message);
    }

    public LocalDate getDateAllowBlankOrThrow(List<String> names) {
        return ValueUtility.toDateAllowBlankOrThrow(getStringOrNull(names));
    }

    // =================================================================================================================

    public LocalTime getTimeOrNull(String name) {
        return ValueUtility.toTimeOrNull(getStringOrNull(name));
    }

    public LocalTime getTimeOrThrow(String name, String message) {
        return ValueUtility.toTimeOrThrow(getStringOrNull(name), message);
    }

    public LocalTime getTimeOrThrow(String name) {
        return ValueUtility.toTimeOrThrow(getStringOrNull(name));
    }

    public LocalTime getTimeAllowBlankOrThrow(String name, String message) {
        return ValueUtility.toTimeAllowBlankOrThrow(getStringOrNull(name), message);
    }

    public LocalTime getTimeAllowBlankOrThrow(String name) {
        return ValueUtility.toTimeAllowBlankOrThrow(getStringOrNull(name));
    }

    public LocalTime getTimeOrNull(List<String> names) {
        return ValueUtility.toTimeOrNull(getStringOrNull(names));
    }

    public LocalTime getTimeOrThrow(List<String> names, String message) {
        return ValueUtility.toTimeOrThrow(getStringOrNull(names), message);
    }

    public LocalTime getTimeOrThrow(List<String> names) {
        return ValueUtility.toTimeOrThrow(getStringOrNull(names));
    }

    public LocalTime getTimeAllowBlankOrThrow(List<String> names, String message) {
        return ValueUtility.toTimeAllowBlankOrThrow(getStringOrNull(names), message);
    }

    public LocalTime getTimeAllowBlankOrThrow(List<String> names) {
        return ValueUtility.toTimeAllowBlankOrThrow(getStringOrNull(names));
    }
}
