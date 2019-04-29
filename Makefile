RPM_VER=$(PEPE_VERSION)
VERSION=${RPM_VER}
RELEASE=$(shell date +%y%m%d%H%M)
SERVICE=api

deploy-snapshot:
	./mvnw clean install -DskipTests deploy:deploy -DaltDeploymentRepository=oss-jfrog::default::http://oss.jfrog.org/artifactory/oss-snapshot-local

package: clean
	./mvnw package -DskipTests

test: clean
	./mvnw test

clean:
	./mvnw clean
	rm -f dists/pepe-${SERVICE}-${RPM_VER}*.rpm

dist: package
	type fpm > /dev/null 2>&1 && \
  echo "#version ${VERSION}" > target/VERSION && \
  git show --summary >> target/VERSION && \
  mkdir -p target/empty && \
  fpm -s dir \
      --rpm-rpmbuild-define '_binaries_in_noarch_packages_terminate_build 0' \
      -t rpm \
      -n "pepe-${SERVICE}" \
      -v ${RPM_VER} \
      --iteration ${RELEASE}.el7 \
      -a noarch \
      --rpm-os linux \
      -m 'A-Team <a-team@corp.globo.com>' \
      --url 'https://pepeproject.github.com' \
      --vendor 'Globo.com' \
      --description "Pepe ${SERVICE} service" \
      --after-install rpms/postinstall \
      --before-remove rpms/preremove \
      --after-remove rpms/postremove \
      -f -p ./dists/pepe-${SERVICE}-${RPM_VER}.el7.noarch.rpm \
              rpms/pepe-profile.sh=/opt/pepe/${SERVICE}/scripts/pepe.sh \
              rpms/pepe@.service=/usr/lib/systemd/system/pepe@.service \
              rpms/log4j.xml=/opt/pepe/${SERVICE}/conf/log4j.xml \
              target/VERSION=/opt/pepe/${SERVICE}/lib/VERSION \
              target/empty/=/opt/logs/pepe/${SERVICE} \
              target/pepe-${SERVICE}-${VERSION}-SNAPSHOT.jar=/opt/pepe/${SERVICE}/lib/pepe.jar

doc:
	cd docs && rm -rf html && doxygen Doxyfile
