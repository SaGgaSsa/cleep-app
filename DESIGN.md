# Design System Strategy: Terminal Kinetic (V2.0)

## 1. Overview & Creative North Star: "The Neon Brutalist"
The design system has evolved from a simple command-line aesthetic into a high-end editorial experience titled **"The Neon Brutalist."** 

This system rejects the "template" look of modern SaaS. It moves away from soft rounded corners and generic shadows, embracing a relentless, sharp-edged kineticism. The Creative North Star is the intersection of a high-performance developer terminal and a luxury fashion lookbook. We achieve this through:
*   **Zero-Radius Geometry:** A strict adherence to 0px corners, creating a sense of structural integrity and digital precision.
*   **Intentional Asymmetry:** Breaking the grid to draw the eye toward "cleeps" (our primary unit of data), using whitespace as a structural element rather than just a gap.
*   **Cyan Kineticism:** Replacing the previous green with a high-energy, electric cyan (#00F0FF) that pierces through the deep #0e0e0e background.

## 2. Colors & Surface Logic
The palette is rooted in deep blacks and vibrant, synthetic cyans. The goal is "Luminance over Decoration."

### The "No-Line" Rule
**Strict Mandate:** Designers are prohibited from using 1px solid borders to section off content. Structural boundaries must be defined solely through background shifts. 
*   Use `surface-container-low` (#131313) against the main `surface` (#0e0e0e) to denote a sidebar.
*   Use `surface-container-highest` (#262626) to highlight a selected "cleep."

### Surface Hierarchy & Nesting
Treat the UI as a series of monolithic slabs. 
*   **Base Layer:** `surface` (#0e0e0e)
*   **Secondary Content:** `surface-container-low` (#131313)
*   **Interactive Cards/Cleeps:** `surface-container` (#1a1919)
*   **Active/Hover States:** `surface-bright` (#2c2c2c)

### The "Glass & Gradient" Rule
To prevent the UI from feeling "flat," use Glassmorphism for floating command pallets or modals. Apply `surface-container` with a 70% opacity and a 20px backdrop-blur. 
*   **Signature Texture:** For primary CTAs, utilize a linear gradient: `primary` (#8ff5ff) to `primary-container` (#00eefc) at a 135-degree angle. This adds a "backlit" effect reminiscent of high-end hardware.

## 3. Typography: Monospace Editorial
The typography system uses **Space Grotesk** across all levels to maintain a "Terminal" soul while providing the legibility of a professional typeface.

| Level | Token | Size | Character |
| :--- | :--- | :--- | :--- |
| **Display** | `display-lg` | 3.5rem | All-caps, -2% tracking. For hero "cleep" counts. |
| **Headline** | `headline-md` | 1.75rem | Bold. Used for section headers. |
| **Title** | `title-sm` | 1.0rem | Medium. Used for individual cleep titles. |
| **Body** | `body-md` | 0.875rem | Regular. The workhorse for cleep descriptions. |
| **Label** | `label-sm` | 0.6875rem | All-caps, +5% tracking. For metadata and timestamps. |

**Hierarchy Note:** Use `on-surface-variant` (#adaaaa) for body text to keep the focus on the `primary` (#8ff5ff) headlines.

## 4. Elevation & Depth
In this design system, depth is a function of light, not physics.

*   **Tonal Layering:** Avoid shadows for standard components. A `surface-container-highest` block sitting on a `surface` background provides all the "lift" required.
*   **Ambient Shadows:** If a modal must float, use a shadow with `blur: 40px`, `spread: 0`, and `color: rgba(0, 240, 255, 0.08)`. This creates a cyan "glow" rather than a grey shadow.
*   **The "Ghost Border" Fallback:** If accessibility requires a stroke, use `outline-variant` (#484847) at 20% opacity. Never use 100% opacity strokes.

## 5. Components

### Cleep Cards
*   **Structure:** No borders. Background: `surface-container-low`. 
*   **Interaction:** On hover, shift background to `surface-container-high`.
*   **Spacing:** Use `spacing-5` (1.1rem) for internal padding.

### Buttons (Kinetic Variants)
*   **Primary:** Background `primary-container` (#00eefc), Text `on-primary` (#005d63). 0px border radius.
*   **Secondary:** Ghost style. No background. Border: 1px `primary` at 30% opacity. Text: `primary`.
*   **Tertiary:** Text-only, All-caps, `label-md` styling with a `_` trailing underscore character to mimic a cursor.

### Input Fields
*   **State:** Underline only. Use `outline` (#767575) for inactive and `primary` (#8ff5ff) for focus.
*   **Caret:** The cursor should be a solid block of `primary` color, blinking at 500ms intervals.

### The Cleep Feed
*   **Spacing:** Use `spacing-12` (2.75rem) between feed items. Do not use divider lines. The negative space is the divider.

## 6. Do’s and Don’ts

### Do
*   **Do** use `primary` (#8ff5ff) sparingly for maximum impact (CTAs, status indicators, active cleeps).
*   **Do** embrace extreme whitespace. If a section feels crowded, double the spacing token.
*   **Do** use `0px` radius for everything. No exceptions.

### Don't
*   **Don't** use standard "Grey" shadows. They muddy the high-contrast aesthetic.
*   **Don't** use icons unless they are strictly functional (e.g., a "Delete" trash can). Favor text-labels like `[DELETE]` or `[EDIT]`.
*   **Don't** refer to user entries as 'ideas'. They are 'cleeps'—fast, sharp, and captured.