package net.ontrack.extension.svnexplorer.model;

import net.ontrack.extension.svn.service.model.SVNLocation;

import java.util.ArrayList;
import java.util.List;

public class SVNTreeNode {

    private final SVNTreeNode parent;
    private final SVNLocation location;
    private final List<SVNTreeNode> children = new ArrayList<>();

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

    @Override
    public String toString() {
        return location.toString();
    }

    public void attachToParent() {
        if (parent != null) {
            parent.children.add(this);
        }
    }
}
