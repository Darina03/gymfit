package daryna.gymfit.dto;

import daryna.gymfit.entities.Coach;
import daryna.gymfit.entities.Field;

public record ResolvedItem(Long id, Coach coach, Field field) {}