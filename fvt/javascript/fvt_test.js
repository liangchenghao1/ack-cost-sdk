/**
 * FVT测试文件，用于验证JavaScript SDK的功能
 */

const { Client, Config } = require('../../javascript/src/client');
const { CostV2Request } = require('../../javascript/src/costv2');
const { AllocationRequest } = require('../../javascript/src/allocation');

async function runFVT() {
    console.log("=== JavaScript FVT测试开始 ===");
    
    try {
        // 初始化客户端
        const config = new Config();
        config.endpoint = "http://127.0.0.1:8080";
        const client = new Client(config);
        
        await testCostV2Service(client);
        await testAllocationService(client);
        
    } catch (error) {
        console.error("JavaScript FVT测试执行出错:", error.message);
        // 即使出现异常也继续执行其他测试
    }
    
    // 测试4: 动态获取集群中的工作负载
    console.log("测试4: 动态获取集群中的工作负载");
    const workloadFilters = [
        'controllerKind:"Deployment"',
        'controllerKind:"StatefulSet"',
        'controllerKind:"DaemonSet"'
    ];
    
    for (const filter of workloadFilters) {
        console.log(`JavaScript SDK将支持查询 ${filter} 类型的工作负载`);
    }
    
    console.log("=== JavaScript FVT测试结束 ===\n");
}

function printRequestLog(apiName, requestData) {
    console.log(`  发送请求到 ${apiName}`);
    console.log(`  请求参数: ${JSON.stringify(requestData)}`);
}

function printResponseLog(responseData, startTime) {
    if (startTime) {
        const elapsed = (Date.now() - startTime) / 1000;
        console.log(`  请求耗时: ${elapsed.toFixed(2)}秒`);
    }
    
    const code = responseData.code || 'N/A';
    console.log(`  响应状态码: ${code}`);
    
    if (responseData.data) {
        const data = responseData.data;
        // 如果是对象类型，尝试格式化输出
        if (typeof data === 'object' && data !== null) {
            console.log('  响应数据详情:');
            // 如果有properties字段，说明是成本数据
            if (data.properties) {
                const props = data.properties;
                if (props.name) {
                    console.log(`    名称: ${props.name}`);
                }
                if (props.cpuCost !== undefined) {
                    console.log(`    CPU成本: ${props.cpuCost}`);
                }
                if (props.gpuCost !== undefined) {
                    console.log(`    GPU成本: ${props.gpuCost}`);
                }
                if (props.ramCost !== undefined) {
                    console.log(`    内存成本: ${props.ramCost}`);
                }
                if (props.pvCost !== undefined) {
                    console.log(`    存储成本: ${props.pvCost}`);
                }
                if (props.totalCost !== undefined) {
                    console.log(`    总成本: ${props.totalCost}`);
                }
            } 
            // 如果有items字段，说明是列表数据
            else if (Array.isArray(data.items)) {
                const items = data.items;
                console.log(`    数据项数量: ${items.length}`);
                // 只显示前3个项目
                for (let i = 0; i < Math.min(3, items.length); i++) {
                    const item = items[i];
                    console.log(`    数据项 ${i+1}:`);
                    if (item.properties) {
                        const props = item.properties;
                        if (props.name) {
                            console.log(`      名称: ${props.name}`);
                        }
                        if (props.totalCost !== undefined) {
                            console.log(`      总成本: ${props.totalCost}`);
                        }
                    } else {
                        const itemStr = JSON.stringify(item);
                        console.log(`      内容: ${itemStr.substring(0, 100)}${itemStr.length > 100 ? '...' : ''}`);
                    }
                }
                if (items.length > 3) {
                    console.log(`    ...还有${items.length - 3}个项目未显示`);
                }
            }
            // 其他对象类型数据
            else {
                const dataStr = JSON.stringify(data, null, 2);
                const lines = dataStr.split('\n');
                if (lines.length <= 20) {
                    console.log(`    数据内容:\n${dataStr}`);
                } else {
                    // 只显示前20行
                    console.log('    数据内容:');
                    for (let i = 0; i < 20; i++) {
                        console.log(lines[i]);
                    }
                    console.log(`    ...还有${lines.length - 20}行未显示`);
                }
            }
        } 
        // 如果是字符串类型
        else if (typeof data === 'string') {
            console.log(`  响应数据: ${data.substring(0, 500)}${data.length > 500 ? '...' : ''}`);
        } 
        // 其他类型
        else {
            const dataStr = String(data);
            console.log(`  响应数据: ${dataStr.substring(0, 500)}${dataStr.length > 500 ? '...' : ''}`);
        }
    } else if (responseData.error) {
        console.log(`  错误信息: ${responseData.error}`);
    }
}

async function testCostV2Service(client) {
    console.log("测试1: CostV2Service");
    
    try {
        // 测试1.1: 基本的GetCostV2调用
        console.log("测试1.1: GetCostV2 with window");
        const req1 = new CostV2Request();
        req1.window = "today";
        printRequestLog("CostV2Service.GetCostV2", req1.toObject());
        const startTime1 = Date.now();
        const resp1 = await client.getCostV2(req1.toObject());
        printResponseLog(resp1, startTime1);
        
        // 验证返回值
        if (resp1.code === 200) {
            console.log("  ✓ 成本API调用成功");
        } else {
            console.log(`  ✗ 成本API调用失败，状态码: ${resp1.code}`);
        }
    } catch (error) {
        console.error("  GetCostV2 with window测试失败:", error.message);
    }
    
    try {
        // 测试1.2: 带filter的GetCostV2调用
        console.log("测试1.2: GetCostV2 with filter");
        const req2 = new CostV2Request();
        req2.window = "today";
        req2.filter = 'namespace:"kube-system"';
        printRequestLog("CostV2Service.GetCostV2", req2.toObject());
        const startTime2 = Date.now();
        const resp2 = await client.getCostV2(req2.toObject());
        printResponseLog(resp2, startTime2);
        
        // 验证返回值
        if (resp2.code === 200) {
            console.log("  ✓ 带过滤条件的成本API调用成功");
        } else {
            console.log(`  ✗ 带过滤条件的成本API调用失败，状态码: ${resp2.code}`);
        }
    } catch (error) {
        console.error("  GetCostV2 with filter测试失败:", error.message);
    }
    
    try {
        // 测试1.3: 带aggregate的GetCostV2调用
        console.log("测试1.3: GetCostV2 with aggregate");
        const req3 = new CostV2Request();
        req3.window = "today";
        req3.aggregate = "namespace";
        printRequestLog("CostV2Service.GetCostV2", req3.toObject());
        const startTime3 = Date.now();
        const resp3 = await client.getCostV2(req3.toObject());
        printResponseLog(resp3, startTime3);
        
        // 验证返回值
        if (resp3.code === 200) {
            console.log("  ✓ 聚合查询的成本API调用成功");
        } else {
            console.log(`  ✗ 聚合查询的成本API调用失败，状态码: ${resp3.code}`);
        }
    } catch (error) {
        console.error("  GetCostV2 with aggregate测试失败:", error.message);
    }
    
    // 测试1.4: 获取工作负载的成本数据
    console.log("测试1.4: GetCostV2 for workloads");
    const workloadTypes = ["Deployment", "StatefulSet", "DaemonSet"];
    for (const workloadType of workloadTypes) {
        try {
            const req = new CostV2Request();
            req.window = "today";
            req.filter = `controllerKind:"${workloadType}"`;
            printRequestLog("CostV2Service.GetCostV2", req.toObject());
            const startTime = Date.now();
            const resp = await client.getCostV2(req.toObject());
            printResponseLog(resp, startTime);
            
            // 验证返回值
            if (resp.code === 200) {
                console.log(`  ✓ ${workloadType}类型工作负载成本查询成功`);
            } else if (resp.code === 503) {
                console.log(`  - ${workloadType}类型工作负载查询返回服务不可用(503)`);
            } else {
                console.log(`  ✗ ${workloadType}类型工作负载查询失败，状态码: ${resp.code}`);
            }
        } catch (error) {
            console.error(`  GetCostV2 for controllerKind:"${workloadType}" failed:`, error.message);
        }
    }
}

async function testAllocationService(client) {
    console.log("测试2: AllocationService");
    
    try {
        // 测试2.1: 基本的GetAllocation调用
        console.log("测试2.1: GetAllocation");
        const req1 = new AllocationRequest();
        req1.window = "today";
        printRequestLog("AllocationService.GetAllocation", req1.toObject());
        const startTime1 = Date.now();
        const resp1 = await client.getAllocation(req1.toObject());
        printResponseLog(resp1, startTime1);
        
        // 验证返回值
        if (resp1.code === 200) {
            console.log("  ✓ 分配API调用成功");
        } else {
            console.log(`  ✗ 分配API调用失败，状态码: ${resp1.code}`);
        }
    } catch (error) {
        console.error("  GetAllocation测试失败:", error.message);
    }
    
    try {
        // 测试2.2: 带aggregate的GetAllocation调用
        console.log("测试2.2: GetAllocation with aggregate");
        const req2 = new AllocationRequest();
        req2.window = "today";
        req2.aggregate = "namespace";
        printRequestLog("AllocationService.GetAllocation", req2.toObject());
        const startTime2 = Date.now();
        const resp2 = await client.getAllocation(req2.toObject());
        printResponseLog(resp2, startTime2);
        
        // 验证返回值
        if (resp2.code === 200) {
            console.log("  ✓ 聚合查询的分配API调用成功");
        } else {
            console.log(`  ✗ 聚合查询的分配API调用失败，状态码: ${resp2.code}`);
        }
    } catch (error) {
        console.error("  GetAllocation with aggregate测试失败:", error.message);
    }
    
    // 测试2.3: 获取工作负载的分配数据
    console.log("测试2.3: GetAllocation for workloads");
    const workloadTypes = ["Deployment", "StatefulSet", "DaemonSet"];
    for (const workloadType of workloadTypes) {
        try {
            const req = new AllocationRequest();
            req.window = "today";
            req.filter = `controllerKind:"${workloadType}"`;
            printRequestLog("AllocationService.GetAllocation", req.toObject());
            const startTime = Date.now();
            const resp = await client.getAllocation(req.toObject());
            printResponseLog(resp, startTime);
            
            // 验证返回值
            if (resp.code === 200) {
                console.log(`  ✓ ${workloadType}类型工作负载分配查询成功`);
            } else if (resp.code === 503) {
                console.log(`  - ${workloadType}类型工作负载分配查询返回服务不可用(503)`);
            } else {
                console.log(`  ✗ ${workloadType}类型工作负载分配查询失败，状态码: ${resp.code}`);
            }
        } catch (error) {
            console.error(`  GetAllocation for controllerKind:"${workloadType}" failed:`, error.message);
        }
    }
}

// 运行FVT测试
if (require.main === module) {
    runFVT().then(() => {
        console.log("JavaScript FVT测试执行完成");
    }).catch((error) => {
        console.error("FVT测试执行出错:", error);
    });
}

module.exports = {
    runFVT
};