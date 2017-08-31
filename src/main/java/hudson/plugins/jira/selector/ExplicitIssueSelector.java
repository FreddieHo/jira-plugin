package hudson.plugins.jira.selector;

import java.util.Collections;
import java.util.List;
import java.util.Set;

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
        String expandedIssueKeys = issueKeys;
        try
        {
            expandedIssueKeys = build.getEnvironment(listener).expand(issueKeys);
        }
        catch (IOException|InterruptedException e)
        {
            e.printStackTrace(listener.getLogger());
        }

        this.jiraIssueKeys = StringUtils.isNotBlank(expandedIssueKeys) ? Lists.newArrayList(expandedIssueKeys.split(",")) : Collections.<String>emptyList();
        this.issueKeys = expandedIssueKeys;
    }

    public ExplicitIssueSelector(List<String> jiraIssueKeys) {
        this.jiraIssueKeys = jiraIssueKeys;
    }

    public ExplicitIssueSelector(){
        this.jiraIssueKeys = Collections.<String>emptyList();
    }

    public void setIssueKeys(String issueKeys){
        String expandedIssueKeys = issueKeys;
        try
        {
            expandedIssueKeys = build.getEnvironment(listener).expand(issueKeys);
        }
        catch (IOException|InterruptedException e)
        {
            e.printStackTrace(listener.getLogger());
        }

        this.jiraIssueKeys = StringUtils.isNotBlank(expandedIssueKeys) ? Lists.newArrayList(expandedIssueKeys.split(",")) : Collections.<String>emptyList();
        this.issueKeys = expandedIssueKeys;
    }

    public String getIssueKeys(){
        return issueKeys;
    }

    @Override
    public Set<String> findIssueIds(Run<?, ?> run, JiraSite site, TaskListener listener) {
        return Sets.newHashSet(jiraIssueKeys);
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<AbstractIssueSelector> {
        @Override
        public String getDisplayName() {
            return Messages.IssueSelector_ExplicitIssueSelector_DisplayName();
        }
    }

}
