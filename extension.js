const vscode = require('vscode');
const fetch = require('node-fetch');
const AdmZip = require('adm-zip');
const fs = require('fs');
const path = require('path');

/**
 * @param {vscode.ExtensionContext} context
 */
function activate(context) {
    let disposable = vscode.commands.registerCommand('spring-initializr.generate', async function () {
        try {
            const groupId = await vscode.window.showInputBox({ prompt: 'Group (e.g. com.example)', value: 'com.example' });
            if (groupId === undefined) { return; }
            const artifactId = await vscode.window.showInputBox({ prompt: 'Artifact (project folder name)', value: 'demo' });
            if (artifactId === undefined) { return; }
            const name = await vscode.window.showInputBox({ prompt: 'Project name', value: artifactId });
            if (name === undefined) { return; }
            const packageName = await vscode.window.showInputBox({ prompt: 'Package name', value: groupId + '.' + artifactId });
            if (packageName === undefined) { return; }
            const deps = await vscode.window.showInputBox({ prompt: 'Dependencies (comma separated, e.g. web,data-jpa)', placeHolder: 'web,data-jpa', value: 'web' });
            if (deps === undefined) { return; }
            const packaging = await vscode.window.showQuickPick(['jar', 'war'], { placeHolder: 'Packaging', canPickMany: false }) || 'jar';
            const javaVersion = await vscode.window.showQuickPick(['17', '11', '21'], { placeHolder: 'Java version', canPickMany: false }) || '17';
            const type = await vscode.window.showQuickPick(['maven-project', 'gradle-project'], { placeHolder: 'Build type', canPickMany: false }) || 'maven-project';

            // choose target folder (workspace folder preferred)
            let targetFolder;
            if (vscode.workspace.workspaceFolders && vscode.workspace.workspaceFolders.length === 1) {
                const useWorkspace = await vscode.window.showQuickPick(['Yes', 'No'], { placeHolder: `Generate into workspace folder: ${vscode.workspace.workspaceFolders[0].uri.fsPath}?` });
                if (useWorkspace === 'Yes') {
                    targetFolder = vscode.workspace.workspaceFolders[0].uri.fsPath;
                }
            }
            if (!targetFolder) {
                const pick = await vscode.window.showOpenDialog({ canSelectFolders: true, openLabel: 'Select target folder' });
                if (!pick || pick.length === 0) { vscode.window.showInformationMessage('Cancelled'); return; }
                targetFolder = pick[0].fsPath;
            }

            vscode.window.withProgress({ location: vscode.ProgressLocation.Notification, title: 'Generating Spring project', cancellable: false }, async (progress) => {
                progress.report({ message: 'Downloading project template...' });
                const baseUrl = 'https://start.spring.io/starter.zip';
                const params = new URLSearchParams();
                params.append('type', type);
                params.append('language', 'java');
                params.append('groupId', groupId);
                params.append('artifactId', artifactId);
                params.append('name', name);
                params.append('packageName', packageName);
                params.append('packaging', packaging);
                params.append('javaVersion', javaVersion);
                if (deps && deps.trim().length > 0) {
                    // Spring Initializr expects dependencies separated by commas
                    params.append('dependencies', deps.replace(/\s+/g, ''));
                }
                const url = `${baseUrl}?${params.toString()}`;

                const res = await fetch(url);
                if (!res.ok) {
                    throw new Error(`Failed to download starter.zip: ${res.status} ${res.statusText}`);
                }
                const buffer = await res.buffer();

                progress.report({ message: 'Extracting project...' });

                const zip = new AdmZip(buffer);
                // Spring Initializr places files inside a base directory (artifactId); extract into target
                // Create a temp directory to extract then move contents to target
                const tmpDir = path.join(targetFolder, `.spring-init-tmp-${Date.now()}`);
                fs.mkdirSync(tmpDir);
                zip.extractAllTo(tmpDir, true);

                // Move extracted files (inside tmpDir/<artifactId> or tmpDir/) into targetFolder
                // If target folder empty, we can move contents; else create a subfolder with artifactId
                const extractedRoot = path.join(tmpDir, artifactId);
                let sourceRoot = fs.existsSync(extractedRoot) ? extractedRoot : tmpDir;

                // If target folder is not empty, create a subfolder for the project
                const targetStat = fs.readdirSync(targetFolder);
                let finalTarget = targetFolder;
                if (targetStat.length > 0) {
                    finalTarget = path.join(targetFolder, artifactId);
                    fs.mkdirSync(finalTarget, { recursive: true });
                }

                // copy files recursively from sourceRoot to finalTarget
                copyRecursiveSync(sourceRoot, finalTarget);

                // cleanup tmpDir
                deleteRecursiveSync(tmpDir);

                vscode.window.showInformationMessage(`Spring project '${artifactId}' generated at ${finalTarget}`);
            });

        } catch (err) {
            vscode.window.showErrorMessage(`Error: ${err.message}`);
            console.error(err);
        }
    });

    context.subscriptions.push(disposable);
}
exports.activate = activate;

function deactivate() {}
exports.deactivate = deactivate;

function copyRecursiveSync(src, dest) {
    const exists = fs.existsSync(src);
    const stats = exists && fs.statSync(src);
    const isDirectory = exists && stats.isDirectory();
    if (isDirectory) {
        fs.mkdirSync(dest, { recursive: true });
        fs.readdirSync(src).forEach(function(childItemName) {
            copyRecursiveSync(path.join(src, childItemName), path.join(dest, childItemName));
        });
    } else {
        fs.copyFileSync(src, dest);
    }
}

function deleteRecursiveSync(p) {
    if (fs.existsSync(p)) {
        fs.readdirSync(p).forEach((file) => {
            const curPath = path.join(p, file);
            if (fs.lstatSync(curPath).isDirectory()) { // recurse
                deleteRecursiveSync(curPath);
            } else { // delete file
                fs.unlinkSync(curPath);
            }
        });
        fs.rmdirSync(p);
    }
}
