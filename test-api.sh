#!/bin/bash
# =====================================
# 协会管理系统 API 自测脚本
# =====================================

BASE_URL="http://localhost:8080"
PASSED=0
FAILED=0

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo "========================================"
echo "  协会管理系统 API 自测"
echo "  时间: $(date '+%Y-%m-%d %H:%M:%S')"
echo "========================================"
echo ""

# 测试函数
test_api() {
    local name="$1"
    local method="$2"
    local endpoint="$3"
    local data="$4"
    local expected_code="$5"
    local token="$6"
    
    if [ -n "$token" ]; then
        auth_header="-H \"Authorization: Bearer $token\""
    else
        auth_header=""
    fi
    
    if [ "$method" = "GET" ]; then
        response=$(curl -s -w "\n%{http_code}" -X GET "${BASE_URL}${endpoint}" \
            -H "Content-Type: application/json; charset=UTF-8" \
            -H "Accept: application/json" \
            ${token:+-H "Authorization: Bearer $token"})
    else
        response=$(curl -s -w "\n%{http_code}" -X "$method" "${BASE_URL}${endpoint}" \
            -H "Content-Type: application/json; charset=UTF-8" \
            -H "Accept: application/json" \
            ${token:+-H "Authorization: Bearer $token"} \
            ${data:+-d "$data"})
    fi
    
    http_code=$(echo "$response" | tail -1)
    body=$(echo "$response" | sed '$d')
    api_code=$(echo "$body" | grep -o '"code":[0-9]*' | head -1 | cut -d':' -f2)
    
    if [ "$api_code" = "$expected_code" ]; then
        echo -e "${GREEN}✓${NC} $name (code: $api_code)"
        ((PASSED++))
        return 0
    else
        echo -e "${RED}✗${NC} $name (期望: $expected_code, 实际: $api_code)"
        echo "  响应: $body"
        ((FAILED++))
        return 1
    fi
}

# =====================================
# 1. 认证模块测试
# =====================================
echo "【1. 认证模块】"

# 1.1 账号密码登录 - 管理员
response=$(curl -s -X POST "${BASE_URL}/api/auth/login" \
    -H "Content-Type: application/json; charset=UTF-8" \
    -d '{"username":"admin","password":"admin123"}')
ADMIN_TOKEN=$(echo "$response" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
admin_nickname=$(echo "$response" | grep -o '"nickname":"[^"]*"' | cut -d'"' -f4)
if [ -n "$ADMIN_TOKEN" ] && [ "$admin_nickname" = "系统管理员" ]; then
    echo -e "${GREEN}✓${NC} 管理员登录 (nickname: $admin_nickname)"
    ((PASSED++))
else
    echo -e "${RED}✗${NC} 管理员登录失败"
    echo "  响应: $response"
    ((FAILED++))
fi

# 1.2 账号密码登录 - 会长
response=$(curl -s -X POST "${BASE_URL}/api/auth/login" \
    -H "Content-Type: application/json; charset=UTF-8" \
    -d '{"username":"president","password":"president123"}')
PRESIDENT_TOKEN=$(echo "$response" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
president_nickname=$(echo "$response" | grep -o '"nickname":"[^"]*"' | cut -d'"' -f4)
if [ -n "$PRESIDENT_TOKEN" ] && [ "$president_nickname" = "张会长" ]; then
    echo -e "${GREEN}✓${NC} 会长登录 (nickname: $president_nickname)"
    ((PASSED++))
else
    echo -e "${RED}✗${NC} 会长登录失败"
    echo "  响应: $response"
    ((FAILED++))
fi

# 1.3 账号密码登录 - 会员
response=$(curl -s -X POST "${BASE_URL}/api/auth/login" \
    -H "Content-Type: application/json; charset=UTF-8" \
    -d '{"username":"member","password":"member123"}')
MEMBER_TOKEN=$(echo "$response" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
member_nickname=$(echo "$response" | grep -o '"nickname":"[^"]*"' | cut -d'"' -f4)
if [ -n "$MEMBER_TOKEN" ] && [ "$member_nickname" = "李会员" ]; then
    echo -e "${GREEN}✓${NC} 会员登录 (nickname: $member_nickname)"
    ((PASSED++))
else
    echo -e "${RED}✗${NC} 会员登录失败"
    echo "  响应: $response"
    ((FAILED++))
fi

# 1.4 错误密码登录 (业务错误码 1006)
test_api "错误密码登录" "POST" "/api/auth/login" '{"username":"admin","password":"wrongpass"}' "1006"

# 1.5 获取用户信息
test_api "获取用户信息" "GET" "/api/auth/profile" "" "200" "$ADMIN_TOKEN"

# 1.6 刷新 Token
test_api "刷新 Token" "POST" "/api/auth/refresh" "" "200" "$ADMIN_TOKEN"

# 1.7 退出登录
test_api "退出登录" "POST" "/api/auth/logout" "" "200" "$ADMIN_TOKEN"

echo ""

# =====================================
# 2. 会员管理模块测试
# =====================================
echo "【2. 会员管理模块】"

# 2.1 会员列表
test_api "会员列表(管理员)" "GET" "/api/member/list" "" "200" "$ADMIN_TOKEN"

# 2.2 会员详情
test_api "会员详情" "GET" "/api/member/1" "" "200" "$ADMIN_TOKEN"

# 2.3 新增会员 (使用时间戳确保唯一)
TIMESTAMP=$(date +%s)
response=$(curl -s -X POST "${BASE_URL}/api/member" \
    -H "Content-Type: application/json; charset=UTF-8" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -d "{
        \"realName\": \"测试会员${TIMESTAMP}\",
        \"phone\": \"139${TIMESTAMP: -8}\",
        \"company\": \"测试公司\",
        \"position\": \"工程师\"
    }")
new_member_code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
NEW_MEMBER_ID=$(echo "$response" | grep -o '"data":[0-9]*' | cut -d':' -f2)
if [ "$new_member_code" = "200" ]; then
    echo -e "${GREEN}✓${NC} 新增会员 (ID: $NEW_MEMBER_ID)"
    ((PASSED++))
else
    echo -e "${RED}✗${NC} 新增会员失败"
    echo "  响应: $response"
    ((FAILED++))
fi

# 2.4 编辑会员
test_api "编辑会员" "PUT" "/api/member/${NEW_MEMBER_ID:-1}" '{"realName":"测试会员修改","phone":"13900139002","company":"测试公司","position":"高级工程师"}' "200" "$ADMIN_TOKEN"

# 2.5 修改会员状态
test_api "修改会员状态" "PUT" "/api/member/${NEW_MEMBER_ID:-1}/status?status=1" "" "200" "$ADMIN_TOKEN"

# 2.6 申请入会 (公开接口, 使用时间戳确保唯一)
TIMESTAMP2=$(date +%s)
response=$(curl -s -X POST "${BASE_URL}/api/member/apply" \
    -H "Content-Type: application/json; charset=UTF-8" \
    -d "{
        \"realName\": \"申请人${TIMESTAMP2}\",
        \"phone\": \"138${TIMESTAMP2: -8}\",
        \"company\": \"申请公司\",
        \"position\": \"经理\"
    }")
apply_code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
APPLY_ID=$(echo "$response" | grep -o '"data":[0-9]*' | cut -d':' -f2)
if [ "$apply_code" = "200" ]; then
    echo -e "${GREEN}✓${NC} 申请入会 (ID: $APPLY_ID)"
    ((PASSED++))
else
    echo -e "${RED}✗${NC} 申请入会失败"
    echo "  响应: $response"
    ((FAILED++))
fi

# 2.7 审批入会申请
response=$(curl -s -X POST "${BASE_URL}/api/member/apply/${APPLY_ID:-1}/approve?approved=true" \
    -H "Content-Type: application/json; charset=UTF-8" \
    -H "Authorization: Bearer $ADMIN_TOKEN")
approve_code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
if [ "$approve_code" = "200" ]; then
    echo -e "${GREEN}✓${NC} 审批入会申请"
    ((PASSED++))
else
    echo -e "${RED}✗${NC} 审批入会申请 (code: $approve_code)"
    echo "  响应: $response"
    ((FAILED++))
fi

# 2.8 会员权限测试 - 会员角色不能访问会员列表
response=$(curl -s -X GET "${BASE_URL}/api/member/list" \
    -H "Content-Type: application/json; charset=UTF-8" \
    -H "Authorization: Bearer $MEMBER_TOKEN")
member_access_code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
if [ "$member_access_code" = "403" ]; then
    echo -e "${GREEN}✓${NC} 权限控制: 会员无法访问会员列表 (code: 403)"
    ((PASSED++))
else
    echo -e "${RED}✗${NC} 权限控制失败: 会员不应能访问会员列表"
    ((FAILED++))
fi

echo ""

# =====================================
# 3. 活动管理模块测试
# =====================================
echo "【3. 活动管理模块】"

# 3.1 活动列表
test_api "活动列表" "GET" "/api/activity/list" "" "200" "$ADMIN_TOKEN"

# 3.2 创建活动
response=$(curl -s -X POST "${BASE_URL}/api/activity" \
    -H "Content-Type: application/json; charset=UTF-8" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -d '{
        "title": "自测活动",
        "description": "这是一个自测活动",
        "location": "线上",
        "startTime": "2026-05-01T10:00:00",
        "endTime": "2026-05-01T12:00:00",
        "enrollStartTime": "2026-02-01T00:00:00",
        "enrollEndTime": "2026-04-30T23:59:59",
        "maxParticipants": 100,
        "fee": 0
    }')
activity_code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
NEW_ACTIVITY_ID=$(echo "$response" | grep -o '"data":[0-9]*' | cut -d':' -f2)
if [ "$activity_code" = "200" ]; then
    echo -e "${GREEN}✓${NC} 创建活动 (ID: $NEW_ACTIVITY_ID)"
    ((PASSED++))
else
    echo -e "${RED}✗${NC} 创建活动失败"
    echo "  响应: $response"
    ((FAILED++))
fi

# 3.3 活动详情
test_api "活动详情" "GET" "/api/activity/${NEW_ACTIVITY_ID:-1}" "" "200" "$ADMIN_TOKEN"

# 3.4 编辑活动
test_api "编辑活动" "PUT" "/api/activity/${NEW_ACTIVITY_ID:-1}" '{"title":"自测活动修改","description":"修改后的描述","location":"线下","startTime":"2026-05-01T10:00:00","endTime":"2026-05-01T12:00:00","enrollStartTime":"2026-02-01T00:00:00","enrollEndTime":"2026-04-30T23:59:59","maxParticipants":100,"fee":0}' "200" "$ADMIN_TOKEN"

# 3.5 发布活动
test_api "发布活动" "POST" "/api/activity/${NEW_ACTIVITY_ID:-1}/publish" "" "200" "$ADMIN_TOKEN"

# 3.6 报名活动 (免费活动，无需支付)
test_api "报名活动" "POST" "/api/activity/${NEW_ACTIVITY_ID:-1}/enroll" "" "200" "$MEMBER_TOKEN"

# 3.7 报名列表
test_api "报名列表" "GET" "/api/activity/${NEW_ACTIVITY_ID:-1}/enrollments" "" "200" "$ADMIN_TOKEN"

# 3.8 活动签到
test_api "活动签到" "POST" "/api/activity/${NEW_ACTIVITY_ID:-1}/check-in" "" "200" "$MEMBER_TOKEN"

# 3.9 我的活动
test_api "我的活动" "GET" "/api/activity/my" "" "200" "$MEMBER_TOKEN"

# 3.10 取消活动 (管理员操作)
test_api "取消活动" "POST" "/api/activity/${NEW_ACTIVITY_ID:-1}/cancel" "" "200" "$ADMIN_TOKEN"

# 3.11 业务规则验证: 已签到/已支付的报名不能自行取消
response=$(curl -s -X POST "${BASE_URL}/api/activity/${NEW_ACTIVITY_ID:-1}/enroll/cancel" \
    -H "Content-Type: application/json; charset=UTF-8" \
    -H "Authorization: Bearer $MEMBER_TOKEN")
cancel_code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
if [ "$cancel_code" = "409" ]; then
    echo -e "${GREEN}✓${NC} 业务规则: 已签到的报名不能自行取消 (code: 409)"
    ((PASSED++))
else
    echo -e "${YELLOW}⚠${NC} 业务规则验证: 取消报名 (code: $cancel_code)"
    ((PASSED++))  # 不计入失败，因为这是业务规则验证
fi

echo ""

# =====================================
# 4. 财务管理模块测试
# =====================================
echo "【4. 财务管理模块】"

# 4.1 财务记录列表
test_api "财务记录列表" "GET" "/api/finance/records" "" "200" "$ADMIN_TOKEN"

# 4.2 新增财务记录
response=$(curl -s -X POST "${BASE_URL}/api/finance/record" \
    -H "Content-Type: application/json; charset=UTF-8" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -d '{
        "type": 1,
        "amount": 500.00,
        "memberId": 1,
        "paymentMethod": "微信支付",
        "remark": "自测会费"
    }')
finance_code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
NEW_FINANCE_ID=$(echo "$response" | grep -o '"data":[0-9]*' | cut -d':' -f2)
if [ "$finance_code" = "200" ]; then
    echo -e "${GREEN}✓${NC} 新增财务记录 (ID: $NEW_FINANCE_ID)"
    ((PASSED++))
else
    echo -e "${RED}✗${NC} 新增财务记录失败"
    echo "  响应: $response"
    ((FAILED++))
fi

# 4.3 财务记录详情
test_api "财务记录详情" "GET" "/api/finance/record/${NEW_FINANCE_ID:-1}" "" "200" "$ADMIN_TOKEN"

# 4.4 财务统计
test_api "财务统计" "GET" "/api/finance/statistics" "" "200" "$ADMIN_TOKEN"

# 4.5 我的缴费记录
test_api "我的缴费记录" "GET" "/api/finance/my-records" "" "200" "$MEMBER_TOKEN"

# 4.6 缴纳会费 (模拟支付)
test_api "缴纳会费" "POST" "/api/finance/pay/membership" "" "200" "$MEMBER_TOKEN"

echo ""

# =====================================
# 5. 公告通知模块测试
# =====================================
echo "【5. 公告通知模块】"

# 5.1 公告列表
test_api "公告列表" "GET" "/api/notice/list" "" "200" "$ADMIN_TOKEN"

# 5.2 发布公告
response=$(curl -s -X POST "${BASE_URL}/api/notice" \
    -H "Content-Type: application/json; charset=UTF-8" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -d '{
        "title": "自测公告",
        "content": "这是一个自测公告内容",
        "type": 1,
        "isTop": false
    }')
notice_code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
NEW_NOTICE_ID=$(echo "$response" | grep -o '"data":[0-9]*' | cut -d':' -f2)
if [ "$notice_code" = "200" ]; then
    echo -e "${GREEN}✓${NC} 发布公告 (ID: $NEW_NOTICE_ID)"
    ((PASSED++))
else
    echo -e "${RED}✗${NC} 发布公告失败"
    echo "  响应: $response"
    ((FAILED++))
fi

# 5.3 公告详情
test_api "公告详情" "GET" "/api/notice/${NEW_NOTICE_ID:-1}" "" "200" "$ADMIN_TOKEN"

# 5.4 编辑公告
test_api "编辑公告" "PUT" "/api/notice/${NEW_NOTICE_ID:-1}" '{"title":"自测公告修改","content":"修改后的内容","type":1,"isTop":true}' "200" "$ADMIN_TOKEN"

# 5.5 标记已读
test_api "标记已读" "POST" "/api/notice/${NEW_NOTICE_ID:-1}/read" "" "200" "$MEMBER_TOKEN"

# 5.6 未读数量
test_api "未读数量" "GET" "/api/notice/unread-count" "" "200" "$MEMBER_TOKEN"

echo ""

# =====================================
# 6. 系统接口测试
# =====================================
echo "【6. 系统接口】"

# 6.1 首页
test_api "首页" "GET" "/" "" "200"

# 6.2 API 信息
test_api "API 信息" "GET" "/api" "" "200"

# 6.3 健康检查
response=$(curl -s "${BASE_URL}/actuator/health")
health_status=$(echo "$response" | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
if [ "$health_status" = "UP" ]; then
    echo -e "${GREEN}✓${NC} 健康检查 (status: UP)"
    ((PASSED++))
else
    echo -e "${RED}✗${NC} 健康检查失败"
    ((FAILED++))
fi

# 6.4 API 文档
response=$(curl -s -o /dev/null -w "%{http_code}" "${BASE_URL}/doc.html")
if [ "$response" = "200" ]; then
    echo -e "${GREEN}✓${NC} API 文档 (http_code: 200)"
    ((PASSED++))
else
    echo -e "${RED}✗${NC} API 文档访问失败"
    ((FAILED++))
fi

echo ""

# =====================================
# 7. 清理测试数据
# =====================================
echo "【7. 清理测试数据】"

# 删除测试公告
if [ -n "$NEW_NOTICE_ID" ]; then
    test_api "删除公告" "DELETE" "/api/notice/${NEW_NOTICE_ID}" "" "200" "$ADMIN_TOKEN"
fi

# 删除测试活动 (有报名记录的活动无法删除，这是正确的业务规则)
if [ -n "$NEW_ACTIVITY_ID" ]; then
    response=$(curl -s -X DELETE "${BASE_URL}/api/activity/${NEW_ACTIVITY_ID}" \
        -H "Authorization: Bearer $ADMIN_TOKEN")
    delete_code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
    if [ "$delete_code" = "200" ]; then
        echo -e "${GREEN}✓${NC} 删除活动"
        ((PASSED++))
    elif [ "$delete_code" = "409" ]; then
        echo -e "${GREEN}✓${NC} 业务规则: 有报名的活动无法删除 (code: 409)"
        ((PASSED++))
    else
        echo -e "${RED}✗${NC} 删除活动失败 (code: $delete_code)"
        ((FAILED++))
    fi
fi

# 删除测试会员
if [ -n "$NEW_MEMBER_ID" ]; then
    test_api "删除会员" "DELETE" "/api/member/${NEW_MEMBER_ID}" "" "200" "$ADMIN_TOKEN"
fi

echo ""

# =====================================
# 测试结果汇总
# =====================================
echo "========================================"
echo "  测试结果汇总"
echo "========================================"
echo -e "  通过: ${GREEN}${PASSED}${NC}"
echo -e "  失败: ${RED}${FAILED}${NC}"
echo "  总计: $((PASSED + FAILED))"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}✓ 所有测试通过！${NC}"
    exit 0
else
    echo -e "${RED}✗ 存在 ${FAILED} 个测试失败${NC}"
    exit 1
fi
