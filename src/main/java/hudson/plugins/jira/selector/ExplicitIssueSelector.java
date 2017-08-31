package hudson.plugins.jira.selector;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.logging.Logger;


import javax.annotation.CheckForNull;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.plugins.jira.Messages;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.jira.JiraSite;

public class ExplicitIssueSelector extends AbstractIssueSelector {

    @CheckForNull
    private List<String> jiraIssueKeys;
    private String issueKeys;

    @DataBoundConstructor
    public ExplicitIssueSelector(String issueKeys) {
        this.jiraIssueKeys = StringUtils.isNotBlank(issueKeys) ? Lists.newArrayList(issueKeys.split(",")) : Collections.<String>emptyList();
        this.issueKeys = issueKeys;
    }

    public ExplicitIssueSelector(List<String> jiraIssueKeys) {
        if (jiraIssueKeys.size() > 0) {
            issueKeys = jiraIssueKeys.get(0);
            for (int i = 1; i < jiraIssueKeys.size(); i++) {
		issueKeys += "," + jiraIssueKeys.get(i);
            }
        }
        this.jiraIssueKeys = jiraIssueKeys;
    }

    public ExplicitIssueSelector(){
        this.jiraIssueKeys = Collections.<String>emptyList();
    }

    public void setIssueKeys(String issueKeys){
        this.jiraIssueKeys = StringUtils.isNotBlank(issueKeys) ? Lists.newArrayList(issueKeys.split(",")) : Collections.<String>emptyList();
        this.issueKeys = issueKeys;
    }

    public String getIssueKeys(){
        return issueKeys;
    }

    @Override
    public Set<String> findIssueIds(Run<?, ?> run, JiraSite site, TaskListener listener) {
        List<String> expandedJiraIssueKeys = Collections.<String>emptyList();
        String expandedIssueKeys = issueKeys;
        if (   StringUtils.isNotBlank(issueKeys)
	    && run != null 
	    && listener != null) {
            try {
                expandedIssueKeys = run.getEnvironment(listener).expand(issueKeys);
            } catch (IOException|InterruptedException e) {
                e.printStackTrace(listener.getLogger());
            }
            expandedJiraIssueKeys = Lists.newArrayList(expandedIssueKeys.split(","));
        }
 
        return Sets.newHashSet(expandedJiraIssueKeys);
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<AbstractIssueSelector> {
        @Override
        public String getDisplayName() {
            return Messages.IssueSelector_ExplicitIssueSelector_DisplayName();
        }
    }

}
