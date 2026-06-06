package com.idp.template.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class GitPushService {

    @Value("${github.token}")
    private String githubToken;

    @Value("${github.owner}")
    private String githubOwner;

    public void push(
            String serviceName
    ) {

        try {

            File projectDir =
                    new File(
                            "generated/" + serviceName
                    );

            log.info(
                    "Starting git push for {}",
                    serviceName
            );

            run(
                    projectDir,
                    "git",
                    "init"
            );

            run(
                    projectDir,
                    "git",
                    "add",
                    "."
            );

            run(
                    projectDir,
                    "git",
                    "config",
                    "user.email",
                    "idp@local.com"
            );

            run(
                    projectDir,
                    "git",
                    "config",
                    "user.name",
                    "IDP Generator"
            );

            run(
                    projectDir,
                    "git",
                    "commit",
                    "-m",
                    "Initial commit"
            );

            run(
                    projectDir,
                    "git",
                    "branch",
                    "-M",
                    "main"
            );

            String remoteUrl =
                    "https://"
                            + githubToken
                            + "@github.com/"
                            + githubOwner
                            + "/"
                            + serviceName
                            + ".git";

            try {

                run(
                        projectDir,
                        "git",
                        "remote",
                        "remove",
                        "origin"
                );

            } catch (Exception ignored) {

                log.info(
                        "No existing remote found"
                );
            }

            run(
                    projectDir,
                    "git",
                    "remote",
                    "add",
                    "origin",
                    remoteUrl
            );

            run(
                    projectDir,
                    "git",
                    "push",
                    "-u",
                    "origin",
                    "main"
            );

            log.info(
                    "Code pushed successfully for {}",
                    serviceName
            );

        } catch (Exception e) {

            log.error(
                    "Git push failed",
                    e
            );

            throw new RuntimeException(
                    "Git push failed",
                    e
            );
        }
    }

    private void run(
            File directory,
            String... command
    ) throws Exception {

        log.info(
                "Executing: {}",
                String.join(
                        " ",
                        command
                )
        );

        ProcessBuilder processBuilder =
                new ProcessBuilder(
                        command
                );

        processBuilder.directory(
                directory
        );

        processBuilder.redirectErrorStream(
                true
        );

        Process process =
                processBuilder.start();

        String output =
                new String(
                        process.getInputStream()
                                .readAllBytes()
                );

        int exitCode =
                process.waitFor();

        log.info(
                "Command output:\n{}",
                output
        );

        if (exitCode != 0) {

            throw new RuntimeException(
                    "Command failed: "
                            + String.join(
                            " ",
                            command
                    )
                            + "\n\nOutput:\n"
                            + output
            );
        }
    }
}