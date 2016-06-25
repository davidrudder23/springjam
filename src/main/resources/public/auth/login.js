// Handle login
function Login($scope, $http, $rootScope) {

    $scope.login = function() {
        localStorage.setItem("email", $scope.email);
        localStorage.setItem("password", $scope.password);

        $http.defaults.headers.common.Authorization = 'Basic ' + btoa(localStorage.getItem("email") + ":" + localStorage.getItem("password"));

        $http.get("/api/band")
            .success(function (data) {
                window.location = "/";
            })
            .error(function (data) {
                $("#login-failed-msg").fadeIn();
            });

        //event.preventDefault();
    }

    $scope.user = {};

    $scope.register = function() {
        console.log($scope.user);

        $http.post("/api/noauth/registerUser", $scope.user)
            .success(function(data){
                $scope.email = $scope.user.email;
                $scope.password = $scope.user.password;
                $scope.login();
            });
    };

}