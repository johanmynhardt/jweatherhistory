#!/usr/bin/env bash

DIST_DIR="jweatherhistory-dist"
LIB_DIR="$DIST_DIR/lib"
START_SCRIPT="$DIST_DIR/jweatherhistory.sh"

echo "==> Building project and gathering dependencies..."
# This builds all modules and copies runtime dependencies to jweatherhistory-run/target/lib
mvn clean package -DskipTests

echo "==> Preparing distribution directory..."
rm -rf "$DIST_DIR"
mkdir -p "$LIB_DIR"

echo "==> Copying dependencies..."
# Copy all resolved Maven dependencies from the run module
if [ -d "jweatherhistory-run/target/lib" ]; then
    cp jweatherhistory-run/target/lib/*.jar "$LIB_DIR/"
else
    echo "Warning: jweatherhistory-run/target/lib not found. Dependencies may be missing."
fi

echo "==> Copying project jars..."
# Copy the built project jars (excluding sources and javadoc)
for jar in $(find . -path "*/target/jweatherhistory-*.jar" ! -name "*-sources.jar" ! -name "*-javadoc.jar"); do
    cp "$jar" "$LIB_DIR/"
done

echo "==> Creating start script..."
cat > "$START_SCRIPT" << 'EOF'
#!/usr/bin/env bash
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
java -cp "$SCRIPT_DIR/lib/*" za.co.johanmynhardt.jweatherhistory.gui.JWeatherHistoryUI
EOF

chmod +x "$START_SCRIPT"

echo "==> Creating archive..."
tar -cjvf jweatherhistory.tar.bz2 "$DIST_DIR"

echo "==> Done! Distribution is ready in '$DIST_DIR' and 'jweatherhistory.tar.bz2'"