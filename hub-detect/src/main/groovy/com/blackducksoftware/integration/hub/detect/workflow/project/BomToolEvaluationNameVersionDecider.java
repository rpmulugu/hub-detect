package com.blackducksoftware.integration.hub.detect.workflow.project;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolEvaluation;
import com.synopsys.integration.util.NameVersion;

public class BomToolEvaluationNameVersionDecider {
    private final Logger logger = LoggerFactory.getLogger(BomToolEvaluationNameVersionDecider.class);

    private final BomToolNameVersionDecider bomToolNameVersionDecider;

    public BomToolEvaluationNameVersionDecider(BomToolNameVersionDecider bomToolNameVersionDecider) {
        this.bomToolNameVersionDecider = bomToolNameVersionDecider;
    }

    public Optional<NameVersion> decideSuggestion(final List<BomToolEvaluation> bomToolEvaluations, String projectBomTool) {
        BomToolGroupType preferredBomToolType = null;
        if (StringUtils.isNotBlank(projectBomTool)) {
            final String projectBomToolFixed = projectBomTool.toUpperCase();
            if (!BomToolGroupType.POSSIBLE_NAMES.contains(projectBomToolFixed)) {
                logger.info("A valid preferred bom tool type was not provided, deciding project name automatically.");
            } else {
                preferredBomToolType = BomToolGroupType.valueOf(projectBomToolFixed);
            }
        }

        final List<BomToolProjectInfo> allBomToolProjectInfo = transformIntoProjectInfo(bomToolEvaluations);
        return bomToolNameVersionDecider.decideProjectNameVersion(allBomToolProjectInfo, preferredBomToolType);
    }

    private List<BomToolProjectInfo> transformIntoProjectInfo(final List<BomToolEvaluation> bomToolEvaluations) {
        return bomToolEvaluations.stream()
                   .filter(it -> it.wasExtractionSuccessful())
                   .filter(it -> it.getExtraction().projectName != null)
                   .map(it -> {
                       final NameVersion nameVersion = new NameVersion(it.getExtraction().projectName, it.getExtraction().projectVersion);
                       final BomToolProjectInfo possibility = new BomToolProjectInfo(it.getBomTool().getBomToolGroupType(), it.getEnvironment().getDepth(), nameVersion);
                       return possibility;
                   })
                   .collect(Collectors.toList());
    }
}