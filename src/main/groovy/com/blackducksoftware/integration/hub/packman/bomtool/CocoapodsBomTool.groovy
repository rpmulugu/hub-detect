package com.blackducksoftware.integration.hub.packman.bomtool

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.bomtool.cocoapods.CocoapodsPackager
import com.blackducksoftware.integration.hub.packman.type.BomToolType
import com.blackducksoftware.integration.hub.packman.util.SourcePathSearcher

@Component
class CocoapodsBomTool extends BomTool {
    @Autowired
    SourcePathSearcher sourcePathSearcher

    @Autowired
    CocoapodsPackager cocoapodsPackager

    private List<String> matchingSourcePaths = []

    BomToolType getBomToolType() {
        return BomToolType.COCOAPODS
    }

    boolean isBomToolApplicable() {
        matchingSourcePaths = sourcePathSearcher.findSourcePathsContainingFilenamePattern('Podfile.lock')

        !matchingSourcePaths.empty
    }

    List<DependencyNode> extractDependencyNodes() {
        List<DependencyNode> projectNodes = []
        matchingSourcePaths.each {
            projectNodes.addAll(cocoapodsPackager.makeDependencyNodes(it))
        }

        projectNodes
    }
}