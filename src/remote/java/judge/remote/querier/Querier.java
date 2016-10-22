package judge.remote.querier;

import judge.remote.RemoteOjAware;
import judge.remote.status.SubmissionRemoteStatus;
import judge.remote.submitter.SubmissionInfo;
import judge.tool.Handler;

/**
 * Short for "Submission remote status querier"
 * Implementation should be stateless.
 * 
 * @author Isun
 *
 */
public interface Querier extends RemoteOjAware {

    void query(SubmissionInfo info, Handler<SubmissionRemoteStatus> handler);
    
}
