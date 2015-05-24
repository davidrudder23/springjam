function Header($scope, $http, $rootScope) {
    $http.get('/api/user')
        .success(function(data) {
            $scope.user= data;
            $scope.loggedIn = true;
            $rootScope.loggedIn = true;
            console.log("logged in");
        })
        .error(function(data){
            $scope.loggedIn = false;
            console.log("!logged in");
        })
    ;
}