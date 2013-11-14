#!/bin/sh

# TODO
# Check dependencies (H2, javamail)
# Update catalina.properties automatically
# Add command line option for tomcat installation directories.

# Help function
show_help() {
        echo "Ontrack install script."
        echo ""
        echo "Available options are:"
        echo "General:"
        echo "    -h, --help                    Displays this help"
        echo "Release numbering:"                         
        echo "    -v,--version=<release>        Ontrack version to install."
}

# Default values
VERSION=

# Remove existing installation (not documented for the moment)
PURGE=

TOMCAT_WEBAPP_ROOT=/var/lib/tomcat7/webapps
TOMCAT_CONTEXT_ROOT=/etc/tomcat7/Catalina/localhost/ontrack.xml

ONTRACK_URL=https://github.com/dcoraboeuf/ontrack/releases/download
ONTRACK_CONTEXT=https://raw.github.com/dcoraboeuf/ontrack/master/pipeline/config/tomcat7/context.xml
ONTRACK_HOME=/opt/ontrack

# Command central
for i in "$@"
do
        case $i in
                -h|--help)
                        show_help
                        exit 0
                        ;;
                -v=*|--version=*)
                        VERSION=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
                        ;;
                -p|--purge)
			PURGE=yes
			;;
                *)
                        echo "Unknown option: $i"
                        show_help
                        exit 1
                ;;
        esac
done

if [ ! -z "${PURGE}" ]
then
	rm -frv  ${ONTRACK_HOME}
	rm -frv ${TOMCAT_CONTEXT_ROOT}
	rm -frv ${TOMCAT_WEBAPP_ROOT}/ontrack.war
	exit 0
fi

if [ -z "${VERSION}" ] 
then
        echo "Version is mandatory."
        show_help
        exit 0
fi

echo "Dowload ontrack-${VERSION}."
wget ${ONTRACK_URL}/ontrack-${VERSION}/ontrack.war -O ontrack-${VERSION}.war

if [ -f ${TOMCAT_CONTEXT_ROOT} ]
then
	echo "Upgrading an existing installation."
else
	mkdir -pv ${ONTRACK_HOME}
	chown -vR tomcat7:tomcat7 ${ONTRACK_HOME} 
	mkdir -pv /etc/tomcat7/Catalina/localhost
	wget ${ONTRACK_CONTEXT} -O /etc/tomcat7/Catalina/localhost/ontrack.xml
fi

cp -v ontrack-${VERSION}.war ${TOMCAT_WEBAPP_ROOT}/ontrack.war
chown -vR tomcat7:tomcat7 ${TOMCAT_WEBAPP_ROOT}/ontrack.war

echo "Clean up temp files."
rm ontrack-${VERSION}.war

echo "Install done."
