# centos-rpm-tomcat
An RPM spec file to install Tomcat on CentOS 7 without  third-party dependencies, fork form http://pkgs.fedoraproject.org/git/rpms/tomcat.git.

The upstream project address: http://pkgs.fedoraproject.org/cgit/rpms/tomcat.git/

### Change

1. Remove third-party dependencies (such as open jdk).

2. Update Tomcat to version 8 on CentOS 7

November 22, 2016.

## Build

### Dependencies
You will need to have these packages install when building on a CentOS 7 based system:

* `rpm-build`
* `rpmdevtools`
* `ant`

### Building the RPM

create the usual directories:

```
build_directories=(
    BUILD
	BUILDROOT
	RPMS
	SOURCES
	SPECS
	SRPMS
)

for directory in ${build_directories[@]}; do
    mkdir "${directory}"
done
```

Now, move the `tomcat.spec` file into the `SPECS` directory.

Next, we trigger the download of the sources needed to build the RPM:
```
spectool --get-files --sourcedir SPECS/tomcat.spec
```

Time to build the package
```
cd SPECS
rpmbuild -ba tomcat.spec
```
