# Design: Modulare Grok-Skills aus picture-copy

## Overview

Extract the picture-copy CLI tool schema into a reusable skill ecosystem under `.grok/skills/`: one orchestrator skill plus seven thematic sub-skills with reference templates.

## PR Plan

### PR 1: Extract reference templates

- **Description:** Extract Java/XML/CMD/SH/properties templates from picture-copy with `{{TOKEN}}` placeholders into sub-skill `references/` directories.
- **Files/components affected:** `.grok/skills/cli-tool-*/references/*`
- **Dependencies:** None

### PR 2: Create sub-skill SKILL.md files

- **Description:** Create SKILL.md for structure, maven, runner, banner, logging, assembly, and registry sub-skills with frontmatter, triggers, and workflow instructions.
- **Files/components affected:** `.grok/skills/cli-tool-structure/SKILL.md`, `.grok/skills/cli-tool-maven/SKILL.md`, `.grok/skills/cli-tool-runner/SKILL.md`, `.grok/skills/cli-tool-banner/SKILL.md`, `.grok/skills/cli-tool-logging/SKILL.md`, `.grok/skills/cli-tool-assembly/SKILL.md`, `.grok/skills/cli-tool-registry/SKILL.md`
- **Dependencies:** PR 1

### PR 3: Create orchestrator skill

- **Description:** Create scaffold-cli-tool orchestrator with project data gathering, sub-skill invocation order, tokens reference, and project checklist.
- **Files/components affected:** `.grok/skills/scaffold-cli-tool/SKILL.md`, `.grok/skills/scaffold-cli-tool/references/project-checklist.md`, `.grok/skills/scaffold-cli-tool/references/tokens.md`
- **Dependencies:** PR 2

### PR 4: Validate and polish skill descriptions

- **Description:** Verify template completeness against picture-copy reference, optimize frontmatter trigger phrases (DE/EN), ensure all placeholder tokens are documented.
- **Files/components affected:** `.grok/skills/**`, `.grok/plans/cli-tool-skills-design.md`
- **Dependencies:** PR 3