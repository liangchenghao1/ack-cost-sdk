# Makefile for FVT tests

.PHONY: help fvt fvt-go fvt-java fvt-javascript fvt-python check-env clean test test-go test-java test-javascript test-python

help: ## 显示帮助信息
	@echo "FVT测试Makefile"
	@echo ""
	@echo "Usage:"
	@echo "  make [target]"
	@echo ""
	@echo "Targets:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

fvt: fvt-go fvt-java fvt-javascript fvt-python ## 运行所有语言的FVT测试

fvt-go: ## 运行Go FVT测试
	@echo "Running Go FVT tests..."
	cd fvt/go && go test -v

fvt-java: ## 运行Java FVT测试
	@echo "Running Java FVT tests..."
	cd fvt/java && \
		if [ ! -f com/aliyun/container/service/cost/sdk/FVTTest.class ] || [ FVTTest.java -nt com/aliyun/container/service/cost/sdk/FVTTest.class ]; then \
			echo "Compiling Java FVT test..."; \
			javac -cp "." com/aliyun/container/service/cost/sdk/*.java || exit 1; \
		fi && \
		java -cp "." com.aliyun.container.service.cost.sdk.FVTTest

fvt-javascript: ## 运行JavaScript FVT测试
	@echo "Running JavaScript FVT tests..."
	cd fvt/javascript && \
		if [ ! -f package.json ]; then \
			echo "Initializing npm package..."; \
			npm init -y; \
		fi && \
		if [ ! -d node_modules ]; then \
			echo "Installing dependencies..."; \
			npm install; \
		fi && \
		node fvt_test.js

fvt-python: ## 运行Python FVT测试
	@echo "Running Python FVT tests..."
	cd fvt/python && \
		if [ ! -f requirements.txt ]; then \
			echo "No requirements.txt found, skipping dependency installation"; \
		else \
			echo "Installing Python dependencies..."; \
			pip install -r requirements.txt; \
		fi && \
		PYTHONPATH=../../../python python3 fvt_test.py

test: test-go test-java test-javascript test-python ## 运行所有语言的单元测试

test-go: ## 运行Go单元测试
	@echo "Running Go unit tests..."
	cd go && go test -v

test-java: ## 运行Java单元测试
	@echo "Running Java unit tests..."
	cd java && \
		if [ -f pom.xml ] && mvn -v > /dev/null 2>&1; then \
			echo "Using Maven to run Java tests..."; \
			mvn test; \
		else \
			echo "Maven not found or pom.xml not available, running unit tests directly..."; \
			mkdir -p target/test-classes; \
			javac -cp "src/main/java:/Users/ringtail/.m2/repository/junit/junit/4.13.2/junit-4.13.2.jar:/Users/ringtail/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar" \
			      -d target/test-classes \
			      src/test/java/com/aliyun/container/service/cost/sdk/*.java || exit 1; \
			java -cp "target/test-classes:src/main/java:/Users/ringtail/.m2/repository/junit/junit/4.13.2/junit-4.13.2.jar:/Users/ringtail/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar" \
			     org.junit.runner.JUnitCore \
			     com.aliyun.container.service.cost.sdk.ClientTest \
			     com.aliyun.container.service.cost.sdk.ConfigTest; \
		fi

test-javascript: ## 运行JavaScript单元测试
	@echo "Running JavaScript unit tests..."
	cd javascript && \
		if [ -f package.json ] && npm -v > /dev/null 2>&1 && grep -q '"test"' package.json; then \
			echo "Using npm test to run JavaScript tests..."; \
			npm test; \
		else \
			echo "npm test not configured, running unit tests with jest..."; \
			if ! npm list jest >/dev/null 2>&1; then \
				echo "Installing jest..."; \
				npm install --save-dev jest; \
			fi; \
			npx jest test/; \
		fi

test-python: ## 运行Python单元测试
	@echo "Running Python unit tests..."
	cd python && \
		if [ ! -d venv ] ; then \
			echo "Creating virtual environment..."; \
			python3 -m venv venv; \
		fi; \
		if [ ! -f venv/bin/activate ]; then \
			echo "Error: Failed to create virtual environment"; \
			exit 1; \
		fi; \
		echo "Activating virtual environment and running tests..."; \
		. venv/bin/activate && pip install pytest && PYTHONPATH=. python -m pytest test/; \

check-env: ## 检查运行环境
	@echo "Checking environment..."
	@echo "Go version: $$(go version 2>/dev/null || echo 'Not found')"
	@echo "Java version: $$(java -version 2>&1 | head -1 || echo 'Not found')"
	@echo "Maven version: $$(mvn -v 2>/dev/null | head -1 || echo 'Not found')"
	@echo "Node version: $$(node --version 2>/dev/null || echo 'Not found')"
	@echo "Python version: $$(python3 --version 2>/dev/null || echo 'Not found')"
	@echo "Environment check completed."

clean: ## 清理测试产物
	@echo "Cleaning test artifacts..."
	find . -name "*.class" -type f -delete
	find . -name "node_modules" -type d -exec rm -rf {} + 2>/dev/null || true
	find . -name "package-lock.json" -type f -delete
	rm -rf python/venv
	@echo "Clean completed."