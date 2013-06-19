package net.ontrack.extension.git.client.plot;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revplot.AbstractPlotRenderer;
import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.jgit.revplot.PlotCommitList;

public class GitPlotRenderer extends AbstractPlotRenderer<GitPlotLane, GitColor> {

    private final GitPlot plot;

    public GitPlotRenderer(PlotCommitList<GitPlotLane> commitList) {
        // Plot to create
        plot = new GitPlot();
        // Loops over the commits
        for (PlotCommit<GitPlotLane> commit : commitList) {
            paintCommit(commit, 100);
        }
    }

    public GitPlot getPlot() {
        return plot;
    }

    @Override
    protected int drawLabel(int x, int y, Ref ref) {
        // FIXME Implement net.ontrack.extension.git.client.plot.GitPlotRenderer.drawLabel
        return 0;
    }

    @Override
    protected GitColor laneColor(GitPlotLane myLane) {
        // FIXME Implement net.ontrack.extension.git.client.plot.GitPlotRenderer.laneColor
        return null;
    }

    @Override
    protected void drawLine(GitColor gitColor, int x1, int y1, int x2, int y2, int width) {
        // FIXME Implement net.ontrack.extension.git.client.plot.GitPlotRenderer.drawLine

    }

    @Override
    protected void drawCommitDot(int x, int y, int w, int h) {
        // FIXME Implement net.ontrack.extension.git.client.plot.GitPlotRenderer.drawCommitDot

    }

    @Override
    protected void drawBoundaryDot(int x, int y, int w, int h) {
        // FIXME Implement net.ontrack.extension.git.client.plot.GitPlotRenderer.drawBoundaryDot

    }

    @Override
    protected void drawText(String msg, int x, int y) {
        // FIXME Implement net.ontrack.extension.git.client.plot.GitPlotRenderer.drawText

    }
}
