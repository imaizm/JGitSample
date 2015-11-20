package imaizm.jgit_sample;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 * JGitSampleApp
 */
public class App 
{
	public static void main(String[] args) throws IOException, GitAPIException
	{
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository =
			builder.setGitDir(new File(".git")).readEnvironment().findGitDir().build();
		
		Git git = new Git(repository);
		
		List<Ref> branchList =
			git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
		
		System.out.println("[branch]");
		for (Ref branch : branchList) {
			System.out.println(branch.getName());
		}
		
		Iterable<RevCommit> revCommitList = 
			git.log().all().call();
		
		System.out.println();
		System.out.println("[revCommit]");
		for (RevCommit revCommit : revCommitList) {
			System.out.println(
				revCommit.getName() + " " +
				revCommit.getCommitterIdent() + " " + 
				revCommit.getShortMessage());
		}
		
		
		RevWalk revWalk = new RevWalk(repository);
		revWalk.markStart(revWalk.parseCommit(repository.resolve("HEAD")));
		
		DiffFormatter diffFormatter = new DiffFormatter(System.out);
		diffFormatter.setRepository(repository);
		
		
		System.out.println();
		System.out.println("[revCommit]");
		for (Iterator<RevCommit> it = revWalk.iterator(); it.hasNext(); ) {
			RevCommit revCommit = it.next();
			
			System.out.println(
				revCommit.getName() + " " +
				revCommit.getCommitterIdent() + " " + 
				revCommit.getShortMessage());
			
			RevWalk refWalkforDiff = new RevWalk(repository);
			refWalkforDiff.markStart(
				refWalkforDiff.parseCommit(
					repository.resolve(revCommit.getName())));
			
			RevTree fromTree = revCommit.getTree();
			RevTree toTree = null;
			refWalkforDiff.next();
			RevCommit prevRevCommit = refWalkforDiff.next();
			if (prevRevCommit != null) {
				toTree = prevRevCommit.getTree();
			}
			refWalkforDiff.close();
			refWalkforDiff.dispose();
			
			List<DiffEntry> diffEntryList =
				diffFormatter.scan(toTree, fromTree);
			
			for (DiffEntry diffEntry : diffEntryList) {
				System.out.println(
					diffEntry.getChangeType() + " " +
					diffEntry.getNewPath());
			}
		}
		
		revWalk.close();
		revWalk.dispose();
	}
}
