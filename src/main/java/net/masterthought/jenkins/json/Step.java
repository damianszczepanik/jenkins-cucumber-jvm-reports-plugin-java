package net.masterthought.jenkins.json;

import net.masterthought.jenkins.ConfigurationOptions;

import java.util.Arrays;
import java.util.List;

public class Step {

    private String name;
    private String keyword;
    private String line;
    private Result result;
    private Row[] rows;

    public Step(String name, String keyword, String line) {
        this.name = name;
        this.keyword = keyword;
        this.line = line;
    }

    public Row[] getRows() {
        return rows;
    }

    public boolean hasRows() {
        boolean result = false;
        if (rows != null) {
            if (rows.length > 0) {
                result = true;
            }
        }
        return result;
    }

    public Long getDuration() {
        if(result == null){
         return 1L;
        } else {
            return result.getDuration();
        }
    }

    private Util.Status getInternalStatus() {
        if(result == null){
            return Util.Status.MISSING;
        } else {
        return Util.resultMap.get(result.getStatus());
        }
    }

    public Util.Status getStatus() {
        Util.Status status = getInternalStatus();
        Util.Status result = status;

        if (ConfigurationOptions.skippedFailsBuild()) {
            if (status == Util.Status.SKIPPED || status == Util.Status.FAILED) {
                result = Util.Status.FAILED;
            }
        }

        if (ConfigurationOptions.undefinedFailsBuild()) {
            if (status == Util.Status.UNDEFINED || status == Util.Status.FAILED) {
                result = Util.Status.FAILED;
            }
        }

        if (status == Util.Status.FAILED) {
            result = Util.Status.FAILED;
        }
        return result;
    }

    public String getDataTableClass() {
        String content = "";
        Util.Status status = getStatus();
        if (status == Util.Status.FAILED) {
            content = "failed";
        } else if (status == Util.Status.PASSED) {
            content = "passed";
        } else if (status == Util.Status.SKIPPED) {
            content = "skipped";
        } else {
            content = "";
        }
        return content;
    }

    public String getName() {
        String content = "";
        if (getStatus() == Util.Status.FAILED) {
            String errorMessage = result.getErrorMessage();
            if (getInternalStatus() == Util.Status.SKIPPED) {
                errorMessage = "Mode: Skipped causes Failure<br/><span class=\"skipped\">This step was skipped</span>";
            }
            if (getInternalStatus() == Util.Status.UNDEFINED) {
                errorMessage = "Mode: Not Implemented causes Failure<br/><span class=\"undefined\">This step is not yet implemented</span>";
            }
            content = Util.result(getStatus()) + "<span class=\"step-keyword\">" + keyword + " </span><span class=\"step-name\">" + name + "</span>" + "<div class=\"step-error-message\"><pre>" + formatError(errorMessage) + "</pre></div>" + Util.closeDiv();
        } else if(getStatus() == Util.Status.MISSING){
            String errorMessage = "<span class=\"missing\">Result was missing for this step</span>";
            content = Util.result(getStatus()) + "<span class=\"step-keyword\">" + keyword + " </span><span class=\"step-name\">" + name + "</span>" + "<div class=\"step-error-message\"><pre>" + formatError(errorMessage) + "</pre></div>" + Util.closeDiv();
            return null;
        } else {
            content = Util.result(getStatus()) + "<span class=\"step-keyword\">" + keyword + " </span><span class=\"step-name\">" + name + "</span>" + Util.closeDiv();
        }
        return content;
    }

    private String formatError(String errorMessage){
      String result = errorMessage;
      if(errorMessage != null || !errorMessage.isEmpty()){
          result = errorMessage.replaceAll("\\\\n","<br/>");
      }
        return result;
    }

    public Result getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "Step{" +
                "name='" + name + '\'' +
                ", keyword='" + keyword + '\'' +
                ", line='" + line + '\'' +
                ", result=" + result +
                ", rows=" + (rows == null ? null : Arrays.asList(rows)) +
                '}';
    }
}
