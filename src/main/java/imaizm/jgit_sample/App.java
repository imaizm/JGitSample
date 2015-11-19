package imaizm.jgit_sample;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 * JGitSampleApp
 */
public class App 
{
	public static void main( String[] args ) throws IOException, GitAPIException
	{
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository =
			builder.setGitDir(new File(".git")).readEnvironment().findGitDir().build();
		
		Git git = new Git(repository);
		
		List<Ref> branchList =
			git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
		
		System.out.println( "[branch]" );
		for (Ref branch : branchList) {
			System.out.println(branch.getName());
		}
		
		Iterable<RevCommit> revCommitList = 
			git.log().all().call();
		
		System.out.println( "[revCommit]" );
		for (RevCommit revCommit : revCommitList) {
			System.out.println(
				revCommit.getName() + " " +
				revCommit.getAuthorIdent() + " " +
				revCommit.getCommitterIdent() + " " + 
				revCommit.getShortMessage());
		}
	}
}
