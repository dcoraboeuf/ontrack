package net.ontrack.extension.git.client.plot;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revplot.AbstractPlotRenderer;
import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.jgit.revplot.PlotCommitList;
import org.eclipse.jgit.revplot.PlotLane;

public class GitPlotRenderer extends AbstractPlotRenderer<PlotLane, GColor> {

    public static final int DEFAULT_ROW_HEIGHT = 24;

    private final GPlot plot;
    private final int rowHeight = DEFAULT_ROW_HEIGHT;
    private int rowIndex;

    public GitPlotRenderer(PlotCommitList<PlotLane> commitList) {
        // Plot to create
        plot = new GPlot();
        // Loops over the commits
        rowIndex = 0;
        for (PlotCommit<PlotLane> commit : commitList) {
            paintCommit(commit, rowHeight);
            rowIndex++;
        }
    }

    public GPlot getPlot() {
        return plot;
    }

    @Override
    protected int drawLabel(int x, int y, Ref ref) {
        // FIXME Implement net.ontrack.extension.git.client.plot.GitPlotRenderer.drawLabel
        return 0;
    }

    @Override
    protected GColor laneColor(PlotLane myLane) {
        if (myLane == null) {
            return new GColor(0);
        } else {
            return new GColor(myLane.getPosition());
        }
    }

    @Override
    protected void drawLine(GColor color, int x1, int y1, int x2, int y2, int width) {
        plot.add(GLine.of(color, point(x1, y1), point(x2, y2), width));
    }

    @Override
    protected void drawCommitDot(int x, int y, int w, int h) {
        // TODO Color
        plot.add(GOval.of(GColor.of(0), point(x, y), GDim.of(w, h)));

    }

    @Override
    protected void drawBoundaryDot(int x, int y, int w, int h) {
        // FIXME Implement net.ontrack.extension.git.client.plot.GitPlotRenderer.drawBoundaryDot

    }

    @Override
    protected void drawText(String msg, int x, int y) {
        // FIXME Implement net.ontrack.extension.git.client.plot.GitPlotRenderer.drawText

    }

    private GPoint point(int x1, int y1) {
        return GPoint.of(x1, y1).ty(rowIndex * rowHeight);
    }
}
