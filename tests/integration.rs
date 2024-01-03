extern crate assert_cmd;

#[cfg(test)]
mod integration {
    use assert_cmd::Command;

    #[test]
    fn application_writes_version_to_stdout_if_called_with_version_flag() {
        let mut cmd = Command::cargo_bin("toochwaerg").unwrap();
        cmd.arg("--version")
        .assert()
        .stdout("toochwaerg 1.0.0\n")
        .success();
    }
}
