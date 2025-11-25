
# VS Code Spring Initializr Extension (minimal)

This is a minimal VS Code extension that generates a Spring Boot project using Spring Initializr (https://start.spring.io) and extracts it into your chosen folder.

This workspace contains:
- `extension.js` — the extension implementation (JavaScript)
- `package.json` — extension manifest and runtime dependencies
- `test_download.js` — a small smoke-test script that downloads and extracts a starter ZIP

## Quick start (what I ran here)

1. Open this folder in VS Code.
2. Install dependencies:

```powershell
cd 'c:\Users\Admin\New folder\internship1'
npm install
```

3. (Optional smoke test I ran) From the project folder I ran the included `test_download.js` script which:
	- downloaded a starter ZIP from start.spring.io
	- saved it as `starter.zip`
	- extracted files to `starter-extract`

```powershell
node test_download.js
```

4. To test the extension UI: press F5 in VS Code to launch an Extension Development Host. In the new window open the command palette (Ctrl+Shift+P) and run: `Spring Initializr: Generate Project`.

## Behavior and notes
- The extension prompts for Group, Artifact, Name, Package, Dependencies (comma-separated), Packaging, Java version and build type.
- It downloads `starter.zip` from `https://start.spring.io/starter.zip` with the chosen parameters and extracts the contents into your selected folder.
- If the target folder is non-empty the extension creates a subfolder named after the artifact to avoid overwriting existing files.

## Files created by the smoke test
- `starter.zip` — the downloaded archive (can be removed)
- `starter-extract/` — extracted project contents

## .gitignore
The repository had a Java-style `.gitignore`; I appended ignores for Node and the generated artifacts. See the `.gitignore` in the repo.

## Next steps / improvements
- Add dependency autocomplete (pull available starter metadata from the Initializr API).
- Add better validation and collision handling (ask before overwriting files).
- Support toggles for Gradle vs Maven settings and additional Initializr parameters.
- Add automated tests (unit + integration) to exercise the extraction logic.

If you'd like, I can add dependency autocomplete or create a small integration test that runs the extension command programmatically.
