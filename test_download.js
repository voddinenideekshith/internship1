const fetch = require('node-fetch');
const AdmZip = require('adm-zip');
const fs = require('fs');
const path = require('path');

(async () => {
  try {
    const params = new URLSearchParams({
      type: 'maven-project',
      language: 'java',
      groupId: 'com.example',
      artifactId: 'demo',
      name: 'demo',
      packageName: 'com.example.demo',
      packaging: 'jar',
      javaVersion: '17',
      dependencies: 'web'
    });
    const url = `https://start.spring.io/starter.zip?${params.toString()}`;
    console.log('Downloading', url);
    const res = await fetch(url);
    if (!res.ok) throw new Error(`Download failed: ${res.status} ${res.statusText}`);
    const buffer = await res.buffer();
    const zipPath = path.join(__dirname, 'starter.zip');
    fs.writeFileSync(zipPath, buffer);
    console.log('Saved zip to', zipPath);

    const extractDir = path.join(__dirname, 'starter-extract');
    const zip = new AdmZip(buffer);
    zip.extractAllTo(extractDir, true);
    console.log('Extracted to', extractDir);
  } catch (err) {
    console.error('Error during smoke test:', err);
    process.exit(1);
  }
})();
