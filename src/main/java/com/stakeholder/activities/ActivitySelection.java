package com.stakeholder.activities;

import java.util.List;

record ActivitySelection(GeneratorFamily family, List<String> flavors, String kind) {
    ActivitySelection {
        flavors = List.copyOf(flavors);
    }
}
