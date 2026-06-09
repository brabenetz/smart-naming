package net.brabenetz.tools.smart.naming.config;

import net.brabenetz.tools.smart.naming.exception.SmartNamingException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Component
@ConfigurationProperties(prefix = "smartnaming")
public class SmartNamingConfigs {

  private String usedModel;
  private Map<String, LlmModelConfig> models = new HashMap<>();
  private String systemPrompt;
  private int maxRetries = 3;
  private String targetFilenamePattern;
  private Pattern compiledTargetFilenamePattern;

  public LlmModelConfig resolveActiveModel() {
    return resolveModel(usedModel);
  }

  public LlmModelConfig resolveModel(String modelKey) {
    if (StringUtils.isBlank(modelKey)) {
      throw new SmartNamingException("No model key configured. Set smartnaming.used-model or pass --smartnaming.used-model=<key>");
    }
    LlmModelConfig modelConfig = models.get(modelKey);
    if (modelConfig == null) {
      throw new SmartNamingException(String.format("Unknown model key '%s'. Available keys: %s", modelKey, models.keySet()));
    }
    return modelConfig;
  }

  public String getUsedModel() {
    return usedModel;
  }

  public void setUsedModel(String usedModel) {
    this.usedModel = usedModel;
  }

  public Map<String, LlmModelConfig> getModels() {
    return models;
  }

  public void setModels(Map<String, LlmModelConfig> models) {
    this.models = models;
  }

  public String getSystemPrompt() {
    return systemPrompt;
  }

  public void setSystemPrompt(String systemPrompt) {
    this.systemPrompt = systemPrompt;
  }

  public int getMaxRetries() {
    return maxRetries;
  }

  public void setMaxRetries(int maxRetries) {
    this.maxRetries = maxRetries;
  }

  public String getTargetFilenamePattern() {
    return targetFilenamePattern;
  }

  public void setTargetFilenamePattern(String targetFilenamePattern) {
    this.targetFilenamePattern = targetFilenamePattern;
    this.compiledTargetFilenamePattern = null;
  }

  public Pattern getCompiledTargetFilenamePattern() {
    if (compiledTargetFilenamePattern == null) {
      if (StringUtils.isBlank(targetFilenamePattern)) {
        throw new SmartNamingException("The smartnaming.target-filename-pattern is required");
      }
      String pattern = targetFilenamePattern;
      try {
        compiledTargetFilenamePattern = Pattern.compile(pattern);
      } catch (PatternSyntaxException e) {
        throw new SmartNamingException(String.format(
            "Invalid smartnaming.target-filename-pattern: %s", pattern), e);
      }
    }
    return compiledTargetFilenamePattern;
  }
}