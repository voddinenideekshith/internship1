const fetch = require('node-fetch');
const AdmZip = require('adm-zip');
const fs = require('fs');
const path = require('path');

(async () => {
  try {
    // Selected dependencies:
    // Spring Web, Spring Data JPA, Spring Security, OAuth2 Client, Redis, Thymeleaf
    // Mapping to Initializr dependency IDs: web, data-jpa, security, oauth2-client, data-redis, thymeleaf

    const params = new URLSearchParams({
      type: 'maven-project',
      language: 'java',
      groupId: 'com.example',
      artifactId: 'springdemo',
      name: 'spring-demo',
      packageName: 'com.example.springdemo',
      packaging: 'jar',
      javaVersion: '17',
      dependencies: 'web,data-jpa,security,oauth2-client,data-redis,thymeleaf'
    });

    const url = `https://start.spring.io/starter.zip?${params.toString()}`;
    console.log('Downloading', url);
    const res = await fetch(url);
    if (!res.ok) throw new Error(`Download failed: ${res.status} ${res.statusText}`);
    const buffer = await res.buffer();

    const zipPath = path.join(__dirname, 'spring-selected.zip');
    fs.writeFileSync(zipPath, buffer);
    console.log('Saved zip to', zipPath);

    const extractDir = path.join(__dirname, 'generated-spring');
    const zip = new AdmZip(buffer);
    zip.extractAllTo(extractDir, true);
    console.log('Extracted to', extractDir);

    // Show a few key files if present
    const projectRoot = path.join(extractDir, 'springdemo');
    if (fs.existsSync(projectRoot)) {
      const pom = path.join(projectRoot, 'pom.xml');
      const mainApp = path.join(projectRoot, 'src', 'main', 'java');
      console.log('Project root detected at', projectRoot);
      if (fs.existsSync(pom)) console.log('Found pom.xml');
      if (fs.existsSync(mainApp)) console.log('Found src/main/java');
    }

    console.log('Done.');
  } catch (err) {
    console.error('Error during generation:', err);
    process.exit(1);
  }
})();
