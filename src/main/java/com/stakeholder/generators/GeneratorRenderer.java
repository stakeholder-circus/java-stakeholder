package com.stakeholder.generators;

import com.stakeholder.activities.GeneratorFamily;
import java.util.Set;

public interface GeneratorRenderer {
    Set<GeneratorFamily> families();

    GeneratorRenderResult render(GeneratorFamily family, GeneratorRenderContext context);
}
