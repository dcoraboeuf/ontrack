package net.ontrack.extension.svnexplorer.model;

import com.google.common.base.Predicate;
import net.ontrack.extension.svn.service.model.SVNLocation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SVNTreeNode {

    private final SVNTreeNode parent;
    private final SVNLocation location;
    private final List<SVNTreeNode> children = new ArrayList<>();
    private boolean tag;
    private boolean closed;

    public SVNTreeNode(SVNLocation location) {
        this(null, location);
    }

    public SVNTreeNode(SVNTreeNode parent, SVNLocation location) {
        this.parent = parent;
        this.location = location;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public SVNTreeNode getParent() {
        return parent;
    }

    public List<SVNTreeNode> getChildren() {
        return children;
    }

    public SVNLocation getLocation() {
        return location;
    }

    public boolean isTag() {
        return tag;
    }

    public void setTag(boolean tag) {
        this.tag = tag;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    @Override
    public String toString() {
        return location.toString();
    }

    public void attachToParent() {
        if (parent != null) {
            parent.children.add(this);
        }
    }

    public void visitBottomUp(SVNTreeNodeVisitor visitor) {
        for (SVNTreeNode child : children) {
            child.visitBottomUp(visitor);
        }
        visitor.visit(this);
    }

    public void visitUpBottom(SVNTreeNodeVisitor visitor) {
        visitor.visit(this);
        for (SVNTreeNode child : children) {
            child.visitUpBottom(visitor);
        }
    }

    public boolean all(final Predicate<SVNTreeNode> predicate) {
        final AtomicBoolean result = new AtomicBoolean(true);
        visitBottomUp(new SVNTreeNodeVisitor() {
            @Override
            public void visit(SVNTreeNode svnTreeNode) {
                result.set(result.get() && predicate.apply(svnTreeNode));
            }
        });
        return result.get();
    }

    public void filterChildren(Predicate<SVNTreeNode> predicate) {
        Iterator<SVNTreeNode> i = children.iterator();
        while (i.hasNext()) {
            if (!predicate.apply(i.next())) {
                i.remove();
            }
        }
    }
}
